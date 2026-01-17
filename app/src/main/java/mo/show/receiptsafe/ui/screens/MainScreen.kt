package mo.show.receiptsafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mo.show.receiptsafe.ui.components.CurvedTopAppBar
import mo.show.receiptsafe.ui.screens.home.HomeScreen
import mo.show.receiptsafe.ui.screens.list.ProductListScreen
import mo.show.receiptsafe.ui.screens.settings.SettingsScreen
import mo.show.receiptsafe.ui.theme.BrandColor


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
            CurvedTopAppBar(
                title = title,
                actions = {
                    IconButton(onClick = onAddProductClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Product",
                            tint = Color.White,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .padding(4.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp) // Float it slightly for modern look
                    .height(80.dp)
            ) {
                // Background Card
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Side
                        IconButton(
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "My Items",
                                    tint = if (selectedTab == 1) BrandColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.4f),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f)) // Space for FAB

                        // Right Side
                        IconButton(
                            onClick = { selectedTab = 2 },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = if (selectedTab == 2) BrandColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.4f),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }

                // Centered Elevated FAB
                FloatingActionButton(
                    onClick = { selectedTab = 0 },
                    containerColor = if (selectedTab == 0) BrandColor else MaterialTheme.colorScheme.surface, // Invert when selected? Or keep prominent
                    contentColor = if (selectedTab == 0) Color.White else BrandColor,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(64.dp)
                        .padding(top = 4.dp), // Slight adjust
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(32.dp)
                    )
                }
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

