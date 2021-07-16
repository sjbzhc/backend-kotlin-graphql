package com.puroayurveda.puroayurveda.datafetchers

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.puroayurveda.generated.client.*
import com.puroayurveda.generated.types.InputUser
import com.puroayurveda.generated.types.User
import com.puroayurveda.puroayurveda.infra.memory.UsersServiceMemory
import com.puroayurveda.puroayurveda.scalars.DateTimeScalarRegistration
import com.puroayurveda.puroayurveda.services.UsersService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(classes = [
    DgsAutoConfiguration::class,
    DateTimeScalarRegistration::class,
    UsersDataFetcher::class
])
class UsersDataFetcherTest {

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    lateinit var usersService: UsersService

    var usersDataService: UsersService = UsersServiceMemory()
    var usersDataFetcher: UsersDataFetcher = UsersDataFetcher(usersDataService)

    @BeforeEach()
    fun before() {
        usersDataService = UsersServiceMemory()
        usersDataFetcher = UsersDataFetcher(usersDataService)

        `when`(usersService.getAll()).thenAnswer { listOf(
            User.newBuilder()
                .id("someId")
                .name("name")
                .username("userName")
                .email("mail@mail.com")
                .build()) }
    }

    @Test
    fun getAllUsersEmpty() {
        val graphQLQueryRequest = GraphQLQueryRequest(
            UserGraphQLQuery.newRequest().build(),
            UserProjectionRoot().name()
        )

        val users: List<User> = dgsQueryExecutor.executeAndExtractJsonPath(
            """
            {
                users {
                    name
                }
            }
        """.trimIndent(), "data.users"
        )
        Assertions.assertThat(users.size).isEqualTo(1)
    }

    @Test
    fun addUserMutation() {
        val graphQLQueryRequest =
            GraphQLQueryRequest(
                AddUserGraphQLQuery.Builder()
                    .inputUser(InputUser.newBuilder()
                        .name("name1")
                        .userName("userName1")
                        .email("email1@mail.com")
                        .build())
                    .build(),
                AddUserProjectionRoot()
                    .name()
                    .username()
                    .email()
                    .id()
            )

        val executionResult = dgsQueryExecutor.execute(graphQLQueryRequest.serialize())
        Assertions.assertThat(executionResult.errors).isEmpty()
        Assertions.assertThat(executionResult.isDataPresent).isTrue
    }

    @Test
    fun `add a user`() {

        usersDataFetcher.addUser(InputUser.newBuilder().name("test name").userName("test user name").email("test@mail.com").build())

        val users = usersDataFetcher.users()

        Assertions.assertThat(users.size).isGreaterThan(0)
    }

    @Test
    fun `delete a user`() {

        // given
        val user = usersDataFetcher.addUser(
            InputUser.newBuilder().name("test name").userName("test user name").email("test@mail.com").build()
        )

        // when
        usersDataFetcher.deleteUser(user.id)

        val users = usersDataFetcher.users()

        Assertions.assertThat(users.size).isEqualTo(0)
    }
}