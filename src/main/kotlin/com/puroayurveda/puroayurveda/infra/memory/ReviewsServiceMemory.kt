package com.puroayurveda.puroayurveda.infra.memory

import com.github.javafaker.Faker
import com.puroayurveda.generated.types.Review
import com.puroayurveda.generated.types.SubmittedReview
import com.puroayurveda.puroayurveda.services.ReviewsService
import com.puroayurveda.puroayurveda.services.ShowsService
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream
import javax.annotation.PostConstruct
import kotlin.streams.toList

@Service
class ReviewsServiceMemory(private val showsService: ShowsService): ReviewsService {
    private val logger = LoggerFactory.getLogger(ReviewsService::class.java)

    private val reviews = mutableMapOf<Int, MutableList<Review>>()
    private lateinit var reviewsStream : FluxSink<Review>
    private lateinit var reviewsPublisher: ConnectableFlux<Review>

    @PostConstruct
    fun createReviews() {
        val faker = Faker()

        //For each show we generate a random set of reviews.
        showsService.shows().forEach { show ->
            val generatedReviews = IntStream.range(0, faker.number().numberBetween(1, 20)).mapToObj {
                val date =
                    faker.date().past(300, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                Review(
                    faker.name().username(),
                    faker.number().numberBetween(0, 6),
                    OffsetDateTime.of(date, ZoneOffset.UTC)
                )
            }.toList().toMutableList()

            reviews[show.id] = generatedReviews
        }

        val publisher = Flux.create<Review> { emitter ->
            reviewsStream = emitter
        }


        reviewsPublisher = publisher.publish()
        reviewsPublisher.connect()

    }

    override fun reviewsForShow(showId: Int): List<Review>? {
        return reviews[showId]
    }

    override fun reviewsForShows(showIds: List<Int>): Map<Int, List<Review>> {
        logger.info("Loading reviews for shows ${showIds.joinToString()}")

        return reviews.filter { showIds.contains(it.key) }
    }

    override fun saveReview(reviewInput: SubmittedReview) {
        val reviewsForMovie = reviews.getOrPut(reviewInput.showId, { mutableListOf() })
        val review = Review(
            reviewInput.username,
            reviewInput.starScore,
            OffsetDateTime.now()
        )
        reviewsForMovie.add(review)
        reviewsStream.next(review)

        logger.info("Review added {}", review)
    }

    override fun getReviewsPublisher(): Publisher<Review> {
        return reviewsPublisher
    }


}