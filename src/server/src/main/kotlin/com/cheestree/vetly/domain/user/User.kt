package com.cheestree.vetly.domain.user

import com.cheestree.vetly.converter.RoleListConverter
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.http.model.output.user.UserPreview
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users", schema = "vetly")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, updatable = false)
    val uuid: UUID = UUID.randomUUID(),

    @Column(nullable = true, unique = true)
    val uid: String? = null,

    val username: String,
    val email: String,
    val imageUrl: String? = null,
    val phone: Int? = null,
    val birth: LocalDate? = null,

    @Column(columnDefinition = "text[]", insertable = false, updatable = false)
    @Convert(converter = RoleListConverter::class)
    val roles: List<Role>,

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], orphanRemoval = true)
    val animals: MutableList<Animal> = mutableListOf()
) {
    fun toAuthenticatedUser() = AuthenticatedUser(
        id = id,
        uid = uid,
        name = username,
        email = email,
        roles = roles
    )
    fun asPublic() = UserInformation(
        id = id,
        name = username,
        email = email,
        imageUrl = imageUrl,
        roles = roles
    )
    fun asPreview() = UserPreview(
        id = id,
        name = username,
        imageUrl = imageUrl
    )
}