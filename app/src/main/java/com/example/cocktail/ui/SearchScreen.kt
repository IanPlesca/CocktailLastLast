package com.example.cocktail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
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
fun SearchScreen(navController: NavController) {
    var ingredient by remember { mutableStateOf("") }
    var cocktails by remember { mutableStateOf<List<CocktailData>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2193B0), Color(0xFF6DD5ED))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔍 Cerca Cocktail per Ingrediente",
                fontSize = 26.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ingredient,
                onValueChange = { ingredient = it },
                label = { Text("Inserisci un ingrediente (es. Rum)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        isLoading = true
                        val (result, error) = searchCocktailsByIngredient(ingredient)
                        cocktails = result
                        errorMessage = error
                        isLoading = false
                    }
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        isLoading = true
                        val (result, error) = searchCocktailsByIngredient(ingredient)
                        cocktails = result
                        errorMessage = error
                        isLoading = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(10.dp))
            ) {
                Text("Cerca cocktail con questo ingrediente", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            }

            errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 16.sp)
            }

            cocktails?.let { drinks ->
                if (drinks.isEmpty()) {
                    Text(text = "❌ Nessun cocktail trovato", color = Color.White, fontSize = 18.sp)
                } else {
                    Column {
                        drinks.forEach { cocktail ->
                            CocktailItem(cocktail)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("welcomeScreen") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("⬅ Torna alla Home", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

suspend fun searchCocktailsByIngredient(ingredient: String): Pair<List<CocktailData>?, String?> {
    return withContext(Dispatchers.IO) {
        try {
            val url =
                "https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=${ingredient.replace(" ", "_")}"
            val response = URL(url).readText()
            val jsonObject = JSONObject(response)

            val drinksArray = jsonObject.optJSONArray("drinks")
            val cocktails = mutableListOf<CocktailData>()

            if (drinksArray != null) {
                for (i in 0 until drinksArray.length()) {
                    val drink = drinksArray.getJSONObject(i)
                    val name = drink.getString("strDrink")
                    val imageUrl = drink.getString("strDrinkThumb")
                    cocktails.add(CocktailData(name, imageUrl))
                }
            }

            Pair(cocktails, null)
        } catch (e: Exception) {
            Pair(null, "❌ Errore: ${e.message}")
        }
    }
}

@Composable
fun CocktailItem(cocktail: CocktailData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(cocktail.imageUrl),
            contentDescription = cocktail.name,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = cocktail.name, fontSize = 18.sp, color = Color.Black)
    }
}

data class CocktailData(val name: String, val imageUrl: String)
//