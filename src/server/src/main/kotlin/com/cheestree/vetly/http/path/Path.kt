package com.cheestree.vetly.http.path

object Path {
    object Users {
        private const val BASE = "/users"

        const val GET = "$BASE/{userId}"
        const val CREATE = BASE
        const val UPDATE = "$BASE/{userId}"
        const val DELETE = "$BASE/{userId}"

        const val GET_USER_PROFILE = "$BASE/me"
        const val UPDATE_USER_PROFILE = "$BASE/me"
    }

    object Requests {
        const val BASE = "/requests"

        const val GET = "$BASE/{requestId}"
        const val CREATE = BASE
        const val UPDATE = "$BASE/{requestId}"
        const val DELETE = "$BASE/{requestId}"

        const val GET_USER_REQUESTS = "$BASE/me"
        const val GET_ALL = BASE
    }

    object Animals {
        const val BASE = "/animals"

        const val GET = "$BASE/{animalId}"
        const val CREATE = BASE
        const val UPDATE = "$BASE/{animalId}"
        const val DELETE = "$BASE/{animalId}"

        const val GET_USER_ANIMALS = "$BASE/me"
        const val GET_ALL = BASE
    }

    object Clinics {
        const val BASE = "/clinics"

        const val CREATE = BASE
        const val GET = "$BASE/{clinicId}"
        const val UPDATE = "$BASE/{clinicId}"
        const val DELETE = "$BASE/{clinicId}"

        const val GET_ALL = BASE
    }

    object Checkups {
        const val BASE = "/checkups"

        const val CREATE = BASE
        const val GET = "$BASE/{checkupId}"
        const val UPDATE = "$BASE/{checkupId}"
        const val DELETE = "$BASE/{checkupId}"

        const val GET_ALL = BASE
    }

    object Supplies {
        private const val BASE = "/supplies"

        const val CREATE = BASE
        const val GET = "$BASE/{clinicId}/supplies/{supplyId}"
        const val UPDATE = "$BASE/{clinicId}/supply/{supplyId}"
        const val DELETE = "$BASE/{clinicId}/supply/{supplyId}"

        const val GET_CLINIC_SUPPLIES = "$BASE/{clinicId}/supplies"
        const val GET_ALL = BASE
        const val GET_SUPPLY = "$BASE/{supplyId}"
    }

    object Guides {
        const val BASE = "/guides"

        const val CREATE = BASE
        const val GET = "$BASE/{guideId}"
        const val UPDATE = "$BASE/{guideId}"
        const val DELETE = "$BASE/{guideId}"

        const val GET_ALL = BASE
    }

    object Files {
        private const val BASE = "/files"

        const val GET = "$BASE/{fileId}"
        const val UPLOAD = BASE
        const val DELETE = "$BASE/{fileId}"
    }
}
