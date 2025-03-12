package com.example.cocktail.ui

import androidx.compose.foundation.Image //
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
fun CercaPerNome(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var cocktail by remember { mutableStateOf<CocktailData?>(null) }
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
            Text("📝 Cerca cocktail per nome", fontSize = 26.sp, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Inserisci il nome del cocktail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        cocktail = searchCocktailByName(name)
                        isLoading = false
                        if (cocktail == null) {
                            errorMessage = "❌ Nessun cocktail trovato!"
                        }
                    }
                }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        cocktail = searchCocktailByName(name)
                        isLoading = false
                        if (cocktail == null) {
                            errorMessage = "❌ Nessun cocktail trovato!"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Cerca", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) CircularProgressIndicator(color = Color.White)

            errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 16.sp)
            }

            cocktail?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(it.imageUrl),
                        contentDescription = it.name,
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(it.name, fontSize = 22.sp, color = Color.White)
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

suspend fun searchCocktailByName(name: String): CocktailData? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=$name"
            val response = URL(url).readText()
            val jsonObject = JSONObject(response)
            val drinksArray = jsonObject.optJSONArray("drinks")

            if (drinksArray != null && drinksArray.length() > 0) {
                val drink = drinksArray.getJSONObject(0)
                CocktailData(
                    name = drink.getString("strDrink"),
                    imageUrl = drink.getString("strDrinkThumb")
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}


