package com.cheestree.vetly.domain.storage

enum class StorageFolder(
    val path: String,
) {
    ANIMALS("animals"),
    CHECKUPS("checkups"),
    CLINICS("clinics"),
    GUIDES("guides"),
    USERS("users"),
    REQUESTS("requests"),
    ;

    fun withSubfolder(subfolder: String): String = "$path/$subfolder"
}
