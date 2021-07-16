package com.puroayurveda.puroayurveda.datafetchers

import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.puroayurveda.generated.client.AddReviewGraphQLQuery
import com.puroayurveda.generated.client.AddReviewProjectionRoot
import com.puroayurveda.generated.client.ShowsGraphQLQuery
import com.puroayurveda.generated.client.ShowsProjectionRoot
import com.puroayurveda.generated.types.Review
import com.puroayurveda.generated.types.Show
import com.puroayurveda.generated.types.SubmittedReview
import com.puroayurveda.puroayurveda.dataloaders.ReviewsDataLoader
import com.puroayurveda.puroayurveda.infra.memory.UsersServiceMemory
import com.puroayurveda.puroayurveda.scalars.DateTimeScalarRegistration
import com.puroayurveda.puroayurveda.services.ReviewsService
import com.puroayurveda.puroayurveda.services.ShowsService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.OffsetDateTime

@SpringBootTest(classes = [
    ShowsDataFetcher::class,
    ReviewsDataFetcher::class,
    ReviewsDataLoader::class,
    DgsAutoConfiguration::class,
    DateTimeScalarRegistration::class
])
class ShowsDataFetcherTest {

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    lateinit var showsService: ShowsService

    @MockBean
    lateinit var reviewsService: ReviewsService

    @BeforeEach
    fun before() {
        `when`(showsService.shows()).thenAnswer{ listOf(Show.newBuilder().id(1).title("mock title").releaseYear(2020).build())}
        `when`(reviewsService.reviewsForShows(listOf(1))).thenAnswer {
            mapOf(
                Pair(
                    1, listOf(
                        Review("DGS User", 5, OffsetDateTime.now()),
                        Review("DGS User 2", 3, OffsetDateTime.now()),
                    )
                )
            )
        }
    }

    @Test
    fun shows() {
        val graphQLQueryRequest = GraphQLQueryRequest(
            ShowsGraphQLQuery.newRequest().titleFilter("mo").build(),
            ShowsProjectionRoot().title()
        )
//        val titles: List<String> = dgsQueryExecutor.executeAndExtractJsonPath(
//            """
//            {
//                shows {
//                    title
//                    releaseYear
//                }
//            }
//        """.trimIndent(), "data.shows[*].title"
//        )
        val query = graphQLQueryRequest.serialize()
        val titles: List<String> = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.shows[*].title")

        Assertions.assertThat(titles).contains("mock title")

    }

    @Test
    fun showsWithNoFilter() {
        val graphQLQueryRequest = GraphQLQueryRequest(
            ShowsGraphQLQuery.newRequest().build(),
            ShowsProjectionRoot().title()
        )
        val query = graphQLQueryRequest.serialize()
        val titles: List<String> = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.shows[*].title")

        Assertions.assertThat(titles).contains("mock title")

    }

    @Test
    fun showsWithException() {
        `when`(showsService.shows()).thenThrow(RuntimeException("nothing to see here"))

        val result = dgsQueryExecutor.execute(
            """
            {
                shows {
                    title
                    releaseYear
                }
            }
        """.trimIndent()
        )

        Assertions.assertThat(result.errors).isNotEmpty
        Assertions.assertThat(result.errors[0].message).isEqualTo("java.lang.RuntimeException: nothing to see here")
    }

    @Test
    fun showWithReviews() {
        val graphQLQueryRequest =
            GraphQLQueryRequest(
                ShowsGraphQLQuery.Builder()
                    .build(),
                ShowsProjectionRoot()
                    .title()
                    .reviews()
                    .username()
                    .starScore()
            )
        val shows = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            graphQLQueryRequest.serialize(),
            "data.shows[*]",
            object : TypeRef<List<Show>>() {})
        Assertions.assertThat(shows.size).isEqualTo(1)
        Assertions.assertThat(shows[0].reviews?.size).isEqualTo(2)
    }

    @Test
    fun addReviewMutation() {

        val graphQLQueryRequest =
            GraphQLQueryRequest(
                AddReviewGraphQLQuery.Builder()
                    .review(SubmittedReview(1, "testuser", 5))
                    .build(),
                AddReviewProjectionRoot()
                    .username()
                    .starScore()
            )

        val executionResult = dgsQueryExecutor.execute(graphQLQueryRequest.serialize())
        Assertions.assertThat(executionResult.errors).isEmpty()

        Mockito.verify(reviewsService).reviewsForShow(1)
    }
}