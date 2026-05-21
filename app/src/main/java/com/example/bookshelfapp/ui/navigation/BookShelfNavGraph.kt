package com.example.bookshelfapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bookshelfapp.ui.screens.BookDetailsDestination
import com.example.bookshelfapp.ui.screens.BookDetailsScreen
import com.example.bookshelfapp.ui.screens.HomeDestination
import com.example.bookshelfapp.ui.screens.HomeScreen

@Composable
fun BookShelfNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route= HomeDestination.route){
            HomeScreen(
                navigateToBookDetails = {
                    navController.navigate(
                        "${BookDetailsDestination.route}/${it}"
                    )
                }
            )
        }

        composable(
            route= BookDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(BookDetailsDestination.bookIdArg){
                type= NavType.StringType
            })
        ) {
            BookDetailsScreen (
                navigateBack = { navController.navigateUp() },
            )
        }
    }
}