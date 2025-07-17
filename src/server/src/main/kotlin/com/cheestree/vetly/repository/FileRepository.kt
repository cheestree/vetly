package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.file.File
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<File, Long> {
    fun findAllByRawStoragePathIn(storagePaths: List<String>): List<File>
}
