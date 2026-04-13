package org.pabarreiro.barapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.compose.KoinContext
import org.pabarreiro.barapp.presentation.ui.LoginScreen
import org.pabarreiro.barapp.presentation.ui.MenuScreen
import org.pabarreiro.barapp.presentation.ui.TablesScreen

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            val navController = rememberNavController()
            
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("tables") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                
                composable("tables") {
                    TablesScreen(
                        onTableSelected = { tableId ->
                            navController.navigate("menu/$tableId")
                        }
                    )
                }
                
                composable(
                    route = "menu/{tableId}",
                    arguments = listOf(navArgument("tableId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val tableId = backStackEntry.arguments?.getLong("tableId") ?: 0L
                    MenuScreen(
                        mesaId = tableId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}