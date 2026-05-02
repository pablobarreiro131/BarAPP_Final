package org.pabarreiro.barapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.compose.KoinContext
import org.pabarreiro.barapp.presentation.ui.ComandaScreen
import org.pabarreiro.barapp.presentation.ui.LoginScreen
import org.pabarreiro.barapp.presentation.ui.MenuScreen
import org.pabarreiro.barapp.presentation.ui.TablesScreen
import org.pabarreiro.barapp.presentation.ui.theme.BarAppTheme

@Composable
fun App() {
    KoinContext {
        BarAppTheme {
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
                        onTableFree = { mesaId ->
                            navController.navigate("menu/$mesaId")
                        },
                        onTableOccupied = { mesaId ->
                            navController.navigate("comanda/$mesaId")
                        }
                    )
                }

                composable(
                    route = "menu/{mesaId}",
                    arguments = listOf(navArgument("mesaId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val mesaId = backStackEntry.arguments?.getLong("mesaId") ?: 0L
                    MenuScreen(
                        mesaId = mesaId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "comanda/{mesaId}",
                    arguments = listOf(navArgument("mesaId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val mesaId = backStackEntry.arguments?.getLong("mesaId") ?: 0L
                    ComandaScreen(
                        mesaId = mesaId,
                        onBack = { navController.popBackStack() },
                        onAddProducts = {
                            navController.navigate("menu/$mesaId")
                        }
                    )
                }
            }
        }
    }
}