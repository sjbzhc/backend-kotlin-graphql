package com.puroayurveda.puroayurveda.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.puroayurveda.generated.types.Review
import com.puroayurveda.puroayurveda.services.ReviewsService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.streams.toList

@DgsDataLoader(name = "reviews")
class ReviewsDataLoader(val reviewsService: ReviewsService): MappedBatchLoader<Int, List<Review>> {
    /**
     * This method will be called once, even if multiple datafetchers use the load() method on the DataLoader.
     * This way reviews can be loaded for all the Shows in a single call instead of per individual Show.
     */
    override fun load(keys: MutableSet<Int>): CompletionStage<Map<Int, List<Review>>> {
        return CompletableFuture.supplyAsync { reviewsService.reviewsForShows(keys.stream().toList()) }
    }
}