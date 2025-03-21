package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.file.StoredFile
import org.springframework.data.jpa.repository.JpaRepository

interface StoredFileRepository: JpaRepository<StoredFile, Long> {
}