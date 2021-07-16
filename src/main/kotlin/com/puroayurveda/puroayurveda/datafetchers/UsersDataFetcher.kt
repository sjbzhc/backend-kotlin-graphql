package com.puroayurveda.puroayurveda.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.puroayurveda.generated.types.InputUser
import com.puroayurveda.generated.types.User
import com.puroayurveda.puroayurveda.services.UsersService

@DgsComponent
class UsersDataFetcher(private val usersService: UsersService){

    @DgsData(parentType = "Query", field = "users")
    fun users(): List<User> {
        return usersService.getAll()
    }

    @DgsMutation
    // If we pass a list as an input argument, we need to specify it, as the type is not known at runtime:
    // @InputArgument(collectionType = InputClass.class)
    fun addUser(@InputArgument inputUser: InputUser): User {
        return usersService.create(inputUser)
    }

    @DgsMutation
    fun deleteUser(userId: String): Boolean {
        return usersService.delete(userId)
    }
 }