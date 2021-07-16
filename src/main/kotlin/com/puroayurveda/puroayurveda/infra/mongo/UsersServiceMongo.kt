package com.puroayurveda.puroayurveda.infra.mongo

import com.puroayurveda.generated.types.InputUser
import com.puroayurveda.generated.types.User
import com.puroayurveda.puroayurveda.services.UsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
@Primary
class UsersServiceMongo(@Autowired private val mongoOperations: MongoOperations): UsersService {
    override fun get(id: String): User? {
        return mongoOperations.findById(id, User::class.java)
    }

    override fun getAll(): List<User> {
        val query = Query()
        return mongoOperations.find(query, User::class.java)
    }

    override fun create(inputUser: InputUser): User {
        val user = User.newBuilder()
            .name(inputUser.name)
            .username(inputUser.userName)
            .email(inputUser.email)
            .build()
        return mongoOperations.save(user)
    }

    override fun delete(userId: String): Boolean {
        val query = Query()
        query.addCriteria(Criteria.where("id").`is`(userId))
        return mongoOperations.remove(query, User::class.java).deletedCount > 0
    }
}