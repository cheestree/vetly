package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import com.google.cloud.storage.Acl
import com.google.firebase.cloud.StorageClient
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

enum class StorageFolder(val path: String) {
    ANIMALS("animals"),
    CHECKUPS("checkups"),
    CLINICS("clinics"),
    GUIDES("guides"),
    USERS("users"),
}

@Service
class FirebaseStorageService(
    private val appConfig: AppConfig
) {
    private val bucket by lazy {
        StorageClient.getInstance().bucket()
    }

    fun uploadFile(
        file: MultipartFile,
        folder: StorageFolder,
        identifier: String? = null,
        customFileName: String? = null
    ): String {
        validateFile(file)

        val fileName = generateFileName(file, folder, identifier, customFileName)
        val blob = bucket.create(fileName, file.bytes, file.contentType)
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))

        return "https://firebasestorage.googleapis.com/v0/b/${bucket.name}/o/${fileName.replace("/", "%2F")}?alt=media"
    }

    fun uploadMultipleFiles(
        files: List<MultipartFile>,
        folder: StorageFolder,
        identifier: String? = null
    ): List<String> {
        return files.map { file ->
            uploadFile(file, folder, identifier)
        }
    }

    fun deleteFile(fileUrl: String): Boolean {
        return try {
            val fileName = extractFileNameFromUrl(fileUrl)
            val blob = bucket.get(fileName)
            blob?.delete() ?: false
        } catch (e: Exception) {
            println("Failed to delete file: ${e.message}")
            false
        }
    }

    fun deleteFiles(fileUrls: List<String>): List<Boolean> {
        return fileUrls.map { deleteFile(it) }
    }

    fun replaceFile(
        oldFileUrl: String?,
        newFile: MultipartFile,
        folder: StorageFolder,
        identifier: String? = null,
        customFileName: String? = null
    ): String {
        oldFileUrl?.let { deleteFile(it) }
        return uploadFile(newFile, folder, identifier, customFileName)
    }

    fun uploadCheckupFiles(
        files: List<StoredFileInputModel>,
        checkupId: Long
    ): List<Pair<StoredFileInputModel, String>> {
        return files.mapIndexed { index, fileInput ->
            val sanitizedTitle = fileInput.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val fileUrl = uploadFile(
                file = fileInput.file,
                folder = StorageFolder.CHECKUPS,
                identifier = checkupId.toString(),
                customFileName = "${sanitizedTitle}_${index}"
            )
            fileInput to fileUrl
        }
    }

    private fun generateFileName(
        file: MultipartFile,
        folder: StorageFolder,
        identifier: String?,
        customFileName: String?
    ): String {
        val extension = getFileExtension(file)
        val timestamp = System.currentTimeMillis()

        val name = when {
            customFileName != null -> customFileName
            identifier != null -> "${identifier}_${timestamp}"
            else -> "file_${timestamp}_${UUID.randomUUID().toString().take(8)}"
        }

        return "${folder.path}/$name.$extension"
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }

        val allowedTypes = listOf("image/jpeg", "image/png", "image/gif", "image/webp")
        if (file.contentType !in allowedTypes) {
            throw IllegalArgumentException("Invalid file type: ${file.contentType}")
        }

        val maxSize = appConfig.firebase.maxImageSize * 1024 * 1024
        if (file.size > maxSize) {
            throw IllegalArgumentException("File too large: ${file.size} bytes")
        }
    }

    private fun getFileExtension(file: MultipartFile): String {
        return when (file.contentType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            else -> "jpg"
        }
    }

    private fun extractFileNameFromUrl(url: String): String {
        return when {
            url.contains("firebasestorage.googleapis.com") -> {
                url.substringAfter("/o/")
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
}