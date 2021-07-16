package com.puroayurveda.puroayurveda.infra.memory

import com.puroayurveda.generated.types.Course
import com.puroayurveda.generated.types.InputCourse
import com.puroayurveda.generated.types.User
import com.puroayurveda.puroayurveda.services.CoursesService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CoursesServiceMemory: CoursesService {

    private val courses = mutableMapOf<String, Course>()

    override fun getAll(): List<Course> {
        return courses.values.toList()
    }

    override fun create(inputCourse: InputCourse): Course {
        val randomUUID = UUID.randomUUID().toString()
        val course = Course.newBuilder()
            .id(randomUUID)
            .title(inputCourse.title)
//            .creator(inputCourse.creator)
            .build()
        courses.putIfAbsent(course.id, course)

        return course
    }

    override fun enrollInCourse(courseId: String, userId: String): Course {
        val course = courses[courseId]

        if (course != null) {
            val participantsList = course.participants.toMutableList()
//            participantsList.add(userId)
            val updatedCourse = Course
                .newBuilder()
                .id(course.id)
                .creator(course.creator)
                .title(course.title)
                .participants(participantsList)
                .build()
            courses.putIfAbsent(courseId, updatedCourse)
        }
        return Course()
    }

    override fun participantsForCourse(courseId: String): List<User>? {
        TODO("Not yet implemented")
    }

}