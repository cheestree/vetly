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
        const val ASSIGN = "$BASE/{animalId}/assign"
        const val UNASSIGN = "$BASE/{animalId}/unassign"
    }

    object Pets {
        private const val BASE = "$API/pets"

        const val GET_ALL = BASE
        const val CREATE = BASE
        const val GET = "$BASE/{petId}"
        const val UPDATE = "$BASE/{petId}"
        const val DELETE = "$BASE/{petId}"
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
}
