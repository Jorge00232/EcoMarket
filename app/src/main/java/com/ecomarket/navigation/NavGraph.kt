package com.ecomarket.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ecomarket.cart.CartViewModel
import com.ecomarket.store.StoreViewModel
import com.ecomarket.ui.screens.CartScreen
import com.ecomarket.ui.screens.LoginScreen
import com.ecomarket.ui.screens.ProductDetailScreen
import com.ecomarket.ui.screens.ProductListScreen
import com.ecomarket.ui.screens.ProfileScreen
import com.ecomarket.ui.screens.SearchScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val SEARCH = "search"
    const val PROFILE = "profile"
    const val CART = "cart"
    const val PRODUCT_DETAIL = "product/{id}"
    fun productDetail(id: String) = "product/$id"
}

@Composable
fun EcoNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination.shouldShowBottomBar()

    // ViewModels (Room)
    val storeVm: StoreViewModel = viewModel(factory = StoreViewModel.factory)
    val cartVm: CartViewModel = viewModel(factory = CartViewModel.factory)

    Scaffold(
        bottomBar = { if (showBottomBar) BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN,
                modifier = Modifier
            ) {
                // --- Login (sin BottomBar) ---
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onGuest = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // --- Home: catálogo (con Store/Cart VM) ---
                composable(Routes.HOME) {
                    ProductListScreen(
                        onOpenProduct = { id -> navController.navigate(Routes.productDetail(id)) },
                        onOpenCart = { navController.navigate(Routes.CART) },
                        storeVm = storeVm,
                        cartVm = cartVm
                    )
                    // Si quisieras mantener un HomeScreen sencillo, podrías llamarlo aquí también.
                }

                // --- Búsqueda y Perfil (como antes) ---
                composable(Routes.SEARCH) { SearchScreen() }
                composable(Routes.PROFILE) { ProfileScreen() }

                // --- Carrito ---
                composable(Routes.CART) {
                    CartScreen(
                        cartVm = cartVm,
                        onCheckout = { /* TODO: flujo de pago demo */ }
                    )
                }

                // --- Detalle de producto ---
                composable(
                    route = Routes.PRODUCT_DETAIL,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStack ->
                    val id = backStack.arguments?.getString("id").orEmpty()
                    ProductDetailScreen(
                        productId = id,
                        onBack = { navController.popBackStack() },
                        onGoToCart = { navController.navigate(Routes.CART) },
                        storeVm = storeVm,
                        cartVm = cartVm
                    )
                }
            }
        }
    }
}

private fun NavDestination?.shouldShowBottomBar(): Boolean {
    val route = this?.route ?: return false
    return route in listOf(
        Routes.HOME,
        Routes.SEARCH,
        Routes.PROFILE,
        Routes.CART // mostramos la BottomBar también en Carrito
    )
}
