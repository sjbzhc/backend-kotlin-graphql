package com.puroayurveda.puroayurveda.infra.mongo

import com.puroayurveda.generated.types.Course
import com.puroayurveda.generated.types.InputCourse
import com.puroayurveda.generated.types.User
import com.puroayurveda.puroayurveda.services.CoursesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Primary
@Service
class CoursesServiceMongo(@Autowired private val mongoOperations: MongoOperations): CoursesService {
    override fun getAll(): List<Course> {
        val query = Query()
        return mongoOperations.find(query, Course::class.java)
    }

    override fun create(inputCourse: InputCourse): Course {
        return mongoOperations.save(
            Course.newBuilder()
//                .creator(inputCourse.creator)
                .title(inputCourse.title)
                .build())
    }

    override fun enrollInCourse(courseId: String, userId: String): Course {
        val course = mongoOperations.findById(courseId, Course::class.java) ?: return Course.newBuilder().build()

        var participantsList = mutableListOf<String>()
        if (course.participants != null) {
            participantsList = course.participants.toMutableList()
        }

        participantsList.add(userId)

        val updatedCourse = Course.newBuilder()
            .id(course.id)
            .title(course.title)
            .creator(course.creator)
            .participants(participantsList)
            .build()

        val query = Query()
        query.addCriteria(Criteria.where("id").`is`(courseId))

        mongoOperations.findAndReplace(query, updatedCourse)

        return mongoOperations.findById(courseId, Course::class.java)?: return Course.newBuilder().build()
    }

    override fun participantsForCourse(courseId: String): List<User>? {
        var query = Query()
        query.addCriteria(Criteria.where("id").`is`(courseId))

        val course = mongoOperations.findOne(query, Course::class.java)

        query = Query()
        query.addCriteria(Criteria.where("id").`in`(course?.participants))

        return mongoOperations.find(query, User::class.java)

    }

//    override fun participantsForCourses(courseIds: List<String>): Map<String, List<User>> {
//        val query = Query()
//        query.addCriteria(Criteria.where("id").`in`(courseIds))
//
//        val courses = mongoOperations.find(query, Course::class.java)
//
//        val courseToUserListMap = mutableMapOf<String, List<String>>()
//
//        courses.forEach { courseToUserListMap.putIfAbsent(it.id, it.participants) }
//
//        val c
//    }
}