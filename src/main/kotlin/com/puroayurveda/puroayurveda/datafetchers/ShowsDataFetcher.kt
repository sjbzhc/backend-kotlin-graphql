package com.puroayurveda.puroayurveda.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.puroayurveda.generated.types.Show
import com.puroayurveda.puroayurveda.services.ShowsService

@DgsComponent
class ShowsDataFetcher(private val showsService: ShowsService) {

//    @DgsData(parentType = "Query", field = "shows") <---- if field not specified, use fun name
    @DgsQuery
    fun shows(@InputArgument titleFilter: String?): List<Show> {

        return if (titleFilter != null) {
            showsService.shows().filter { it.title.startsWith(titleFilter) }
        } else {
            showsService.shows()
        }
    }
}