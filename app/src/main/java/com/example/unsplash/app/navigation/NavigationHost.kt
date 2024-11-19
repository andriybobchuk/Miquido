package com.studios1299.playwall.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.unsplash.presentation.detail.DetailScreenRoot
import com.example.unsplash.presentation.feed.FeedScreenRoot

/**
 * Pay attention to how Im not passing the navController to every screen and instead just use
 * callbacks to do all the navigation stuff here.
 */
@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Graphs.Main.root
    ) {
        mainGraph(navController)
    }
}

private fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(
        startDestination = Graphs.Main.Screens.feed,
        route = Graphs.Main.root
    ) {
        composable(Graphs.Main.Screens.feed) {
            FeedScreenRoot(
                onPhotoClick = { selectedPhoto ->
                    navController.navigate("${Graphs.Main.Screens.detail}/${selectedPhoto}")
                },
            )
        }
        composable(
            "${Graphs.Main.Screens.detail}/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) { _ ->
            DetailScreenRoot(onBackClick = { navController.navigateUp() })
        }
    }
}


