package com.example.cocktail.ui
//
import androidx.compose.animation.*
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

@Composable
fun WelcomeScreen(navController: NavController) {
    var selectedAge by remember { mutableStateOf(18) }
    var hasDrivingLicense by remember { mutableStateOf<String?>(null) }
    var showSearchOptions by remember { mutableStateOf(false) } //per controllare menu

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2193B0), Color(0xFF6DD5ED))
    )

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
            Text(
                text = "Benvenuto!",
                fontSize = 28.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Seleziona la tua eta':", fontSize = 18.sp, color = Color.White)

            Slider(
                value = selectedAge.toFloat(),
                onValueChange = { selectedAge = it.toInt() },
                valueRange = 5f..105f,
                steps = 100,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Text("Età selezionata: $selectedAge anni", fontSize = 16.sp, color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedAge < 18) {
                Text(
                    "⚠️ Non ti consigliamo di bere alcol!!!! Ti mostreremo solo cocktail analcolici!!!!!!!",
                    color = Color.Yellow,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedAge >= 18) {
                Text("Hai la patente di guida?", fontSize = 18.sp, color = Color.White)

                Row {
                    Button(
                        onClick = { hasDrivingLicense = "Si" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Sì", color = Color.White)
                    }

                    Button(
                        onClick = { hasDrivingLicense = "No" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("No", color = Color.White)
                    }
                }
            }

            if (hasDrivingLicense == "Si") {
                Text(
                    "🚗 Regole per chi guida (Codice della Strada 2024):\n" +
                            "- Neopatentati e conducenti professionali ➝ TASSO 0,0 g/L\n" +
                            "- Tra 0,5 e 0,8 g/L ➝ multa 543-2.170€, sospensione patente 3-6 mesi\n" +
                            "- Tra 0,8 e 1,5 g/L ➝ multa 800-3.200€, arresto fino a 6 mesi, sospensione 6 mesi-1 anno\n" +
                            "- Oltre 1,5 g/L ➝ multa 1.500-6.000€, arresto 6-12 mesi, sospensione 1-2 anni, confisca veicolo\n" +
                            "- In caso di incidente, pene aggravate fino alla revoca della patente.",
                    fontSize = 14.sp,
                    color = Color.Yellow
                )
            } else if (hasDrivingLicense == "No" && selectedAge >= 18) {
                Text(
                    "⚠️ Attenzione! Ricorda di non abusare dell'alcol!",
                    fontSize = 16.sp,
                    color = Color.Yellow
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // per quelli con eta' meno di 18 anni
            if (selectedAge < 18) {
                Button(
                    onClick = { navController.navigate("nonAlcoholicScreen") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .shadow(4.dp, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                ) {
                    Text("🍹 Mostra cocktail analcolici", fontSize = 18.sp, color = Color.White)
                }
            } else {
                // per 18 anni +
                Button(
                    onClick = { showSearchOptions = !showSearchOptions },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .shadow(4.dp, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(if (showSearchOptions) "Chiudi menu" else "Procedi alla ricerca", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))


                // Animazione per il menu a cascata, con chat gpt :)


                AnimatedVisibility(
                    visible = showSearchOptions,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { navController.navigate("searchScreen") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("🔍 Cerca per ingrediente", fontSize = 18.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { navController.navigate("cercaPrimaLettera") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("🔤 Cerca per prima lettera", fontSize = 18.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { navController.navigate("cercaPerNome") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("📝 Cerca per nome", fontSize = 18.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { navController.navigate("cercaCasuale") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("🎲 Cocktail casuale", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
