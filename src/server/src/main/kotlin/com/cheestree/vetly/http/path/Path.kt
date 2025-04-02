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

        const val REQUEST_ROLE = "$BASE/request-role"
    }

    object Animals {
        const val BASE = "$API/animals"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{animalId}"
        const val UPDATE = "$BASE/{animalId}"
        const val DELETE = "$BASE/{animalId}"
    }

    object Clinics {
        const val BASE = "$API/clinics"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{clinicId}"
        const val UPDATE = "$BASE/{clinicId}"
        const val DELETE = "$BASE/{clinicId}"
    }

    object Checkups {
        const val BASE = "$API/checkups"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{checkupId}"
        const val UPDATE = "$BASE/{checkupId}"
        const val DELETE = "$BASE/{checkupId}"
    }

    object Supplies {
        private const val BASE = "$API/supplies"

        const val CREATE = BASE
        const val GET = "$BASE/{clinicId}/supplies/{supplyId}"
        const val UPDATE = "$BASE/{clinicId}/supply/{supplyId}"
        const val DELETE = "$BASE/{clinicId}/supply/{supplyId}"

        const val GET_CLINIC_SUPPLIES = "$BASE/{clinicId}/supplies"
        const val GET_ALL = "$BASE/supplies"
        const val GET_SUPPLY = "$BASE/supplies/{supplyId}"
    }

    object Guides {
        const val BASE = "$API/guides"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{guideId}"
        const val UPDATE = "$BASE/{guideId}"
        const val DELETE = "$BASE/{guideId}"
    }

    object Files {
        private const val BASE = "$API/files"

        const val GET = "$BASE/{fileId}"
        const val UPLOAD = BASE
        const val DELETE = "$BASE/{fileId}"
    }
}
