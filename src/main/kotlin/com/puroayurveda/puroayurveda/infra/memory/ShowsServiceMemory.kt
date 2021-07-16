package com.puroayurveda.puroayurveda.infra.memory

import com.puroayurveda.generated.types.Show
import com.puroayurveda.puroayurveda.services.ShowsService
import org.springframework.stereotype.Service

@Service
class ShowsServiceMemory: ShowsService {
    override fun shows(): List<Show> {
        return listOf(
            Show.newBuilder().id(1).title("Stranger Things").releaseYear(2016).build(),
            Show.newBuilder().id(2).title("Ozark").releaseYear(2017).build(),
            Show.newBuilder().id(3).title("The Crown").releaseYear(2016).build(),
            Show.newBuilder().id(4).title("Dead to Me").releaseYear(2019).build(),
            Show.newBuilder().id(5).title("Orange is the New Black").releaseYear(2013).build(),
        );
    }
}