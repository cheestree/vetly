package com.cheestree.vetly.http.path

object Path {
    object Users {
        private const val BASE = "/users"

        const val GET = "$BASE/profile/{id}"
        const val CREATE = BASE
        const val UPDATE = "$BASE/{id}"
        const val DELETE = "$BASE/{id}"

        const val LOGIN = "$BASE/login"
        const val LOGOUT = "$BASE/logout"
        const val GET_USER_PROFILE = "$BASE/me"
        const val UPDATE_USER_PROFILE = "$BASE/me"
    }

    object Requests {
        const val BASE = "/requests"

        const val GET = "$BASE/{id}"
        const val CREATE = BASE
        const val UPDATE = "$BASE/{id}"
        const val DELETE = "$BASE/{id}"

        const val GET_USER_REQUESTS = "$BASE/me"
        const val GET_ALL = BASE
    }

    object Animals {
        const val BASE = "/animals"

        const val GET = "$BASE/{id}"
        const val CREATE = BASE
        const val UPDATE = "$BASE/{id}"
        const val UPDATE_ANIMAL_IMAGE = "$BASE/{id}/image"
        const val DELETE_ANIMAL = "$BASE/{id}"

        const val GET_USER_ANIMALS = "$BASE/me"
        const val GET_ALL = BASE
    }

    object Clinics {
        const val BASE = "/clinics"

        const val CREATE = BASE
        const val GET = "$BASE/{id}"
        const val UPDATE = "$BASE/{id}"
        const val DELETE = "$BASE/{id}"

        const val GET_ALL = BASE
    }

    object Checkups {
        const val BASE = "/checkups"

        const val CREATE = BASE
        const val GET = "$BASE/{id}"
        const val UPDATE = "$BASE/{id}"
        const val UPDATE_CHECKUP_FILES = "$BASE/{id}"
        const val DELETE = "$BASE/{id}"

        const val GET_ALL = BASE
    }

    object Supplies {
        private const val BASE = "/supplies"

        const val CREATE = BASE
        const val GET = "$BASE/{clinicId}/supplies/{supplyId}"
        const val UPDATE = "$BASE/{clinicId}/supply/{supplyId}"
        const val DELETE = "$BASE/{clinicId}/supply/{supplyId}"

        const val GET_CLINIC_SUPPLIES = "$BASE/{clinicId}/supplies"
        const val ASSOCIATE_SUPPLY = "$BASE/{clinicId}/supplies"
        const val GET_ALL = BASE
        const val GET_SUPPLY = "$BASE/{supplyId}"
    }

    object Guides {
        const val BASE = "/guides"

        const val CREATE = BASE
        const val GET = "$BASE/{id}"
        const val UPDATE = "$BASE/{id}"
        const val DELETE = "$BASE/{id}"

        const val GET_ALL = BASE
    }

    object Files {
        private const val BASE = "/files"

        const val GET = "$BASE/{id}"
        const val UPLOAD = BASE
        const val DELETE = "$BASE/{id}"
    }
}
