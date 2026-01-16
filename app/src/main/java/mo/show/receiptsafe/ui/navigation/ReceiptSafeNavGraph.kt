package mo.show.receiptsafe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Placeholder imports for screens we haven't created yet
import androidx.compose.material3.Text

@Composable
fun ReceiptSafeNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            mo.show.receiptsafe.ui.screens.dashboard.DashboardScreen(
                onAddProductClick = { navController.navigate("add_product") },
                onProductClick = { productId -> navController.navigate("product_detail/$productId") }
            )
        }
        composable("add_product") {
            mo.show.receiptsafe.ui.screens.add.AddProductScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable("product_detail/{productId}") { backStackEntry ->
            mo.show.receiptsafe.ui.screens.detail.ProductDetailScreen(
                onBackClick = { navController.popBackStack() },
                onDeleteSuccess = { navController.popBackStack() }
            )
        }
    }
}
