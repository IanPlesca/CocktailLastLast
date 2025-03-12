package com.example.cocktail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@Composable
fun NonAlcoholicScreen(navController: NavController) {
    var cocktail by remember { mutableStateOf<NonAlcoholicCocktail?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2193B0), Color(0xFF6DD5ED))
    )

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            cocktail = getRandomNonAlcoholicCocktail()
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🍹 Cocktail Analcolico Casuale", fontSize = 26.sp, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (cocktail != null) {
                Image(
                    painter = rememberAsyncImagePainter(cocktail!!.imageUrl),
                    contentDescription = cocktail!!.name,
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(cocktail!!.name, fontSize = 22.sp, color = Color.White)

                Spacer(modifier = Modifier.height(8.dp))

                Text(cocktail!!.description, fontSize = 16.sp, color = Color.White)
            } else {
                Text("❌ Nessun cocktail trovato", color = Color.Red, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            cocktail = getRandomNonAlcoholicCocktail()
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .shadow(4.dp, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("🔄 Nuovo Cocktail", fontSize = 16.sp, color = Color.White)
                }

                Button(
                    onClick = { navController.navigate("welcomeScreen") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .shadow(4.dp, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                ) {
                    Text("⬅ Torna alla Home", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

suspend fun getRandomNonAlcoholicCocktail(): NonAlcoholicCocktail? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic"
            val response = URL(url).readText()
            val jsonObject = JSONObject(response)
            val drinksArray = jsonObject.getJSONArray("drinks")
//
            if (drinksArray.length() > 0) {
                val randomIndex = (0 until drinksArray.length()).random()
                val drink = drinksArray.getJSONObject(randomIndex)
                val drinkDetailsUrl = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=${drink.getString("idDrink")}"
                val drinkDetailsResponse = URL(drinkDetailsUrl).readText()
                val detailsJson = JSONObject(drinkDetailsResponse)
                val detailedDrink = detailsJson.getJSONArray("drinks").getJSONObject(0)
                NonAlcoholicCocktail(
                    name = detailedDrink.getString("strDrink"),
                    imageUrl = detailedDrink.getString("strDrinkThumb"),
                    description = detailedDrink.optString("strInstructions", "Nessuna descrizione disponibile.")
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

data class NonAlcoholicCocktail(
    val name: String,
    val imageUrl: String,
    val description: String = "Nessuna descrizione disponibile."
)
