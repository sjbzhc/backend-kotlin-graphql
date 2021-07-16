package com.puroayurveda.puroayurveda.services

import com.puroayurveda.generated.types.Course
import com.puroayurveda.generated.types.InputCourse
import com.puroayurveda.generated.types.User

interface CoursesService {
    fun getAll(): List<Course>
    fun create(inputCourse: InputCourse): Course
    fun enrollInCourse(courseId: String, userId: String): Course
    fun participantsForCourse(courseId: String): List<User>?
//    fun participantsForCourses(courseIds: List<String>): Map<String, List<User>>
}