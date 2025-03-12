package com.example.cocktail.ui
//
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL



@Composable
fun CercaPrimaLettera(navController: NavController) {
    var letter by remember { mutableStateOf("") }
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
            Text("🔤 Cerca cocktail per lettera", fontSize = 26.sp, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = letter,
                onValueChange = { letter = it.take(1) },
                label = { Text("Inserisci una lettera") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        cocktails = searchCocktailsByLetter(letter)
                        isLoading = false
                        if (cocktails.isNullOrEmpty()) {
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
                        cocktails = searchCocktailsByLetter(letter)
                        isLoading = false
                        if (cocktails.isNullOrEmpty()) {
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

            if (isLoading) CircularProgressIndicator()

            errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 16.sp)
            }

            cocktails?.let { drinks ->
                if (drinks.isNotEmpty()) {
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

suspend fun searchCocktailsByLetter(letter: String): List<CocktailData>? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://www.thecocktaildb.com/api/json/v1/1/search.php?f=$letter"
            val response = URL(url).readText()
            val jsonObject = JSONObject(response)
            val drinksArray = jsonObject.optJSONArray("drinks")
            val cocktails = mutableListOf<CocktailData>()

            drinksArray?.let {
                for (i in 0 until it.length()) {
                    val drink = it.getJSONObject(i)
                    cocktails.add(
                        CocktailData(
                            name = drink.getString("strDrink"),
                            imageUrl = drink.getString("strDrinkThumb")
                        )
                    )
                }
            }

            cocktails
        } catch (e: Exception) {
            null
        }
    }
}
