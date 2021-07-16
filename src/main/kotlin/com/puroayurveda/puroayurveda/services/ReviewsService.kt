package com.puroayurveda.puroayurveda.services

import com.puroayurveda.generated.types.Review
import com.puroayurveda.generated.types.SubmittedReview
import org.reactivestreams.Publisher

interface ReviewsService {
    fun reviewsForShow(showId: Int): List<Review>?
    fun reviewsForShows(showIds: List<Int>): Map<Int, List<Review>>
    fun saveReview(reviewInput: SubmittedReview)
    fun getReviewsPublisher(): Publisher<Review>
}