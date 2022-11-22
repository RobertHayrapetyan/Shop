package com.roberthayrapetyan.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.roberthayrapetyan.shop.ui.navigation.MainNavigation
import com.roberthayrapetyan.shop.ui.navigation.NavigationItem
import com.roberthayrapetyan.shop.ui.theme.ShopTheme
import com.roberthayrapetyan.shop.ui.theme.background
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShopTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {
                    MainNavigation(NavigationItem.ProductListScreen)
                }
            }
        }
    }
}


