package com.puroayurveda.puroayurveda.services

import com.puroayurveda.generated.types.InputUser
import com.puroayurveda.generated.types.User

interface UsersService {
    fun get(id: String): User?
    fun getAll(): List<User>
    fun create(inputUser: InputUser): User
    fun delete(userId: String): Boolean
}