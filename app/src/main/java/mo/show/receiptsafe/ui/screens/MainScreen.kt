package mo.show.receiptsafe.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mo.show.receiptsafe.ui.components.BottomNavBar
import mo.show.receiptsafe.ui.components.CurvedTopAppBar
import mo.show.receiptsafe.ui.screens.home.HomeScreen
import mo.show.receiptsafe.ui.screens.list.ProductListScreen
import mo.show.receiptsafe.ui.screens.settings.SettingsScreen

@Composable
fun MainScreen(
    onAddProductClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = {
            val title = when (selectedTab) {
                0 -> "Dashboard"
                1 -> "My Items"
                2 -> "Settings"
                else -> "Receipt Safe"
            }
            CurvedTopAppBar(title = title)
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> ProductListScreen(onProductClick = onProductClick)
                2 -> SettingsScreen()
            }
        }
    }
}
