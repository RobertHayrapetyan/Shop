package com.roberthayrapetyan.shop.ui.navigation

sealed class NavigationItem(var route: String) {
    object ProductListScreen : NavigationItem("product_list")
    object CartScreen : NavigationItem("cart")
}