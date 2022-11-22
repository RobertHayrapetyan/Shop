package com.roberthayrapetyan.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.roberthayrapetyan.shop.ui.screens.CartScreen
import com.roberthayrapetyan.shop.ui.screens.ProductListScreen
import com.roberthayrapetyan.shop.ui.viewmodels.CartScreenViewModel
import com.roberthayrapetyan.shop.ui.viewmodels.ProductListScreenViewModel

@Composable
fun MainNavigation(navigateTo: NavigationItem) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = navigateTo.route){
        composable(NavigationItem.ProductListScreen.route){
            val viewModel: ProductListScreenViewModel = hiltViewModel()
            ProductListScreen(viewModel, navController)
        }
        composable(NavigationItem.CartScreen.route){
            val viewModel: CartScreenViewModel = hiltViewModel()
            CartScreen(viewModel, navController)
        }
    }
}