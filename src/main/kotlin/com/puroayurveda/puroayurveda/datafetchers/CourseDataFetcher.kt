package com.puroayurveda.puroayurveda.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.puroayurveda.generated.types.Course
import com.puroayurveda.generated.types.InputCourse
import com.puroayurveda.generated.types.User
import com.puroayurveda.puroayurveda.services.CoursesService

@DgsComponent
class CourseDataFetcher(private val coursesService: CoursesService) {

    @DgsData(parentType = "Query")
    fun courses(): List<Course> {
        return coursesService.getAll()
    }

    @DgsData(parentType = "Query", field = "participants")
    fun participants(dfe: DgsDataFetchingEnvironment): List<User>? {
        val course = dfe.getSource<Course>()
        return coursesService.participantsForCourse(course.id)
    }

    @DgsMutation
    fun addCourse(inputCourse: InputCourse): Course {
        return coursesService.create(inputCourse)
    }

    @DgsMutation
    fun enrollInCourse(courseId: String, userId: String): Course {
        return coursesService.enrollInCourse(courseId, userId)
    }
}