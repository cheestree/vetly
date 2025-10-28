package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.file.File
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.repository.FileRepository
import com.google.cloud.storage.Acl
import com.google.firebase.cloud.StorageClient
import org.apache.tika.Tika
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Optional
import java.util.UUID

@Service
class StorageService(
    private val fileRepository: FileRepository,
    private val appConfig: AppConfig,
) {
    private val tika = Tika()
    private val bucket by lazy {
        StorageClient.getInstance().bucket()
    }

    fun uploadFile(
        file: MultipartFile,
        folder: StorageFolder,
        identifier: String? = null,
        customFileName: String? = null,
    ): File {
        val type = validateFile(file)
        val fileName = generateFileName(file, identifier, customFileName)
        val filePath = generateFilePath(folder, fileName)
        val blob = bucket.create(fileName, file.bytes, file.contentType)
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))

        val fileEntity = File(
            rawStoragePath = filePath,
            fileName = fileName,
            mimeType = type,
        )

        return fileRepository.save(fileEntity)
    }

    fun uploadMultipleFiles(
        files: List<MultipartFile>,
        folder: StorageFolder,
        identifier: String? = null,
    ): List<File> =
        files.map { file ->
            uploadFile(file, folder, identifier)
        }

    fun deleteFile(file: File): Boolean =
        try {
            val fileName = extractFileNameFromUrl(file.storagePath)
            val blob = bucket.get(fileName)
            blob?.delete() ?: false
        } catch (e: Exception) {
            println("Failed to delete file: ${e.message}")
            false
        }

    fun deleteFiles(fileUrls: List<File>): List<Boolean> = fileUrls.map { deleteFile(it) }

    fun replaceFile(
        oldFile: File?,
        newFile: MultipartFile,
        folder: StorageFolder,
        identifier: String? = null,
        customFileName: String? = null,
    ): File {
        oldFile?.let { deleteFile(it) }
        return uploadFile(newFile, folder, identifier, customFileName)
    }

    private fun generateFilePath(
        folder: StorageFolder,
        fileName: String,
    ): String = "${folder.path.lowercase(Locale.getDefault())}/$fileName"

    private fun generateFileName(
        file: MultipartFile,
        identifier: String?,
        customFileName: String?,
    ): String {
        val extension = getFileExtension(file)
        val timestamp = System.currentTimeMillis()

        val name =
            when {
                customFileName != null -> customFileName
                identifier != null -> "${identifier}_$timestamp"
                else -> "file_${timestamp}_${UUID.randomUUID().toString().take(8)}"
            }

        return "${name.lowercase(Locale.getDefault())}.$extension"
    }

    private fun validateFile(file: MultipartFile): String {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }

        val type = detectMimeType(file)
        val allowedTypes = listOf("image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf")
        if (type !in allowedTypes) {
            throw IllegalArgumentException("Invalid file type: ${file.contentType}")
        }

        val maxSize = appConfig.firebase.maxImageSize * 1024 * 1024
        if (file.size > maxSize) {
            throw IllegalArgumentException("File too large: ${file.size} bytes")
        }

        return type
    }

    private fun detectMimeType(file: MultipartFile): String {
        val detected = tika.detect(file.inputStream)

        val allowed = listOf("image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf")
        if (detected !in allowed) {
            throw IllegalArgumentException("Unsupported file type: $detected")
        }

        return detected
    }

    private fun getFileExtension(file: MultipartFile): String =
        when (file.contentType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            "application/pdf" -> "pdf"
            else -> "jpg"
        }

    private fun extractFileNameFromUrl(url: String): String =
        when {
            url.contains("firebasestorage.googleapis.com") -> {
                url
                    .substringAfter("/o/")
                    .substringBefore("?")
                    .replace("%2F", "/")
            }

            url.contains("storage.googleapis.com") -> {
                url.substringAfter("${bucket.name}/").substringBefore("?")
            }

            else -> {
                url.substringAfterLast("/").substringBefore("?")
            }
        }
}
