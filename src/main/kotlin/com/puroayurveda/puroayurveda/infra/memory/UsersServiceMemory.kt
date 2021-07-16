package com.puroayurveda.puroayurveda.infra.memory

import com.puroayurveda.generated.types.InputUser
import com.puroayurveda.generated.types.User
import com.puroayurveda.puroayurveda.services.UsersService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsersServiceMemory: UsersService {

        private val users = mutableMapOf<String, User>()

    override fun get(id: String): User? {
        return users[id]
    }

    override fun getAll(): List<User> {
        return users.values.toList()
    }

    override fun create(inputUser: InputUser): User {
        val randomUUID = UUID.randomUUID().toString()
        val user = User.newBuilder()
            .id(randomUUID)
            .name(inputUser.name)
            .username(inputUser.userName)
            .email(inputUser.email).build()

        users.putIfAbsent(user.id, user)

        return user
    }

    override fun delete(userId: String): Boolean {
        val remove = users.remove(userId)
        if (remove != null) {
            return true
        }
        return false
    }
}