package com.puroayurveda.puroayurveda.datafetchers

import com.netflix.graphql.dgs.*
import com.puroayurveda.generated.DgsConstants
import com.puroayurveda.generated.types.Review
import com.puroayurveda.generated.types.Show
import com.puroayurveda.generated.types.SubmittedReview
import com.puroayurveda.puroayurveda.dataloaders.ReviewsDataLoader
import com.puroayurveda.puroayurveda.services.ReviewsService
import graphql.execution.DataFetcherResult
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture

@DgsComponent
class ReviewsDataFetcher(private val reviewsService: ReviewsService) {

    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)

    // CompletableFuture in order to avoid N+1 problem, so all subqueries are batched
    // DataFetcherResult to pass a local context to nested objects (e.g. Show { reviews { starScore }}, starScore has no
    // direct connection to the show anymore)
    fun reviews(dfe: DgsDataFetchingEnvironment): CompletableFuture<DataFetcherResult<List<Review>>> {
        val reviewsDataLoader: DataLoader<Int, List<Review>> = dfe.getDataLoader(ReviewsDataLoader::class.java)

        val show: Show = dfe.getSource()

        val reviews: CompletableFuture<List<Review>> = reviewsDataLoader.load(show.id)

        return reviews.thenApply{ list -> DataFetcherResult.newResult<List<Review>>().data(list).localContext(show).build() }
    }

    @DgsMutation
    fun addReview(@InputArgument review: SubmittedReview): List<Review> {
        reviewsService.saveReview(review)

        return reviewsService.reviewsForShow(review.showId)?: emptyList()
    }

}