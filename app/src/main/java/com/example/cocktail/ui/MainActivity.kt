package com.example.cocktail.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.cocktail.ui.theme.CocktailTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import com.example.cocktail.WelcomeScreen
//import com.example.cocktail.SearchScreen
//import com.example.cocktail.CercaPrimaLettera
//import com.example.cocktail.CercaPerNome
//import com.example.cocktail.CercaCasuale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CocktailTheme {
                val navController = rememberNavController()

                NavHost(navController, startDestination = "welcomeScreen") {
                    composable("welcomeScreen") { WelcomeScreen(navController) }
                    composable("searchScreen") { SearchScreen(navController) }
                    composable("cercaPrimaLettera") { CercaPrimaLettera(navController) }
                    composable("cercaPerNome") { CercaPerNome(navController) }
                    composable("cercaCasuale") { CercaCasuale(navController) }
                    composable("nonAlcoholicScreen") { NonAlcoholicScreen(navController) }

                }
            }
        }
    }
}
