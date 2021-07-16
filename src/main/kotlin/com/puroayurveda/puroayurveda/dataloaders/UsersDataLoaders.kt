//package com.puroayurveda.puroayurveda.dataloaders
//
//import com.netflix.graphql.dgs.DgsDataLoader
//import com.puroayurveda.generated.types.Review
//import com.puroayurveda.generated.types.User
//import com.puroayurveda.puroayurveda.services.CoursesService
//import com.puroayurveda.puroayurveda.services.ReviewsService
//import com.puroayurveda.puroayurveda.services.UsersService
//import org.dataloader.MappedBatchLoader
//import java.util.concurrent.CompletableFuture
//import java.util.concurrent.CompletionStage
//import kotlin.streams.toList
//
//@DgsDataLoader(name = "participants")
//class UsersDataLoaders(val coursesService: CoursesService): MappedBatchLoader<String, List<User>> {
//    /**
//     * This method will be called once, even if multiple datafetchers use the load() method on the DataLoader.
//     * This way reviews can be loaded for all the Shows in a single call instead of per individual Show.
//     */
//    override fun load(keys: MutableSet<String>): CompletionStage<Map<String, List<User>>> {
//        return CompletableFuture.supplyAsync { coursesService.participantsForCourses(keys.stream().toList()) }
//    }
//}