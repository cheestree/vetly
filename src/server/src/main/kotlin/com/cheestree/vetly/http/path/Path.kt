package com.cheestree.vetly.http.path

object Path {
    private const val API = "/api"

    object Users {
        private const val BASE = "$API/users"

        const val GET = "$BASE/{userId}"
        const val CREATE = BASE
        const val LOGIN = "$BASE/login"
        const val LOGOUT = "$BASE/logout"
        const val UPDATE = "$BASE/{userId}"
        const val DELETE = "$BASE/{userId}"
    }

    object Animals {
        private const val BASE = "$API/animals"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{animalId}"
        const val UPDATE = "$BASE/{animalId}"
        const val DELETE = "$BASE/{animalId}"
    }

    object Clinics {
        private const val BASE = "$API/clinics"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{clinicId}"
        const val UPDATE = "$BASE/{clinicId}"
        const val DELETE = "$BASE/{clinicId}"
    }

    object Checkups {
        private const val BASE = "$API/checkups"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{checkupId}"
        const val UPDATE = "$BASE/{checkupId}"
        const val DELETE = "$BASE/{checkupId}"
    }

    object Inventories {
        private const val BASE = "$API/inventories"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{inventoryId}"
        const val UPDATE = "$BASE/{inventoryId}"
        const val DELETE = "$BASE/{inventoryId}"
    }

    object Guides {
        private const val BASE = "$API/guides"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{guideId}"
        const val UPDATE = "$BASE/{guideId}"
        const val DELETE = "$BASE/{guideId}"
    }
}
