package mo.show.receiptsafe.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mo.show.receiptsafe.ui.AppViewModelProvider
import mo.show.receiptsafe.ui.screens.dashboard.DashboardViewModel
import mo.show.receiptsafe.ui.theme.BrandColor
import mo.show.receiptsafe.ui.theme.SecondaryColor

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Total Spending Header
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spending",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$${String.format("%.2f", uiState.totalSpent)}",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 2. Spending Graph (Donut Chart)
        SpendingGraphSection(
            receiptAmount = uiState.spentOnReceipts,
            warrantyAmount = uiState.spentOnWarranties,
            total = uiState.totalSpent
        )

        // 3. Modern Stats Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ModernStatCard(
                title = "This Month",
                amount = uiState.spentThisMonth,
                modifier = Modifier.weight(1f),
                color = BrandColor
            )
            ModernStatCard(
                title = "This Year",
                amount = uiState.spentThisYear,
                modifier = Modifier.weight(1f),
                color = SecondaryColor
            )
        }

        // 4. Recent Activity
        if (uiState.items.isNotEmpty()) {
            Text(
                "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.items.take(3).forEach { product ->
                    RecentItemRow(
                        name = product.name,
                        date = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date(product.purchaseDate)),
                        price = product.price,
                        type = product.type
                    )
                }
            }
        }
    }
}

@Composable
fun SpendingGraphSection(
    receiptAmount: Double,
    warrantyAmount: Double,
    total: Double
) {
    val receiptRatio = if (total > 0) (receiptAmount / total).toFloat() else 0f
    
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Donut Chart
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                DonutChart(
                    receiptRatio = receiptRatio,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = "${(receiptRatio * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            // Legend
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendItem(color = BrandColor, label = "Receipts", amount = receiptAmount)
                LegendItem(color = SecondaryColor, label = "Warranties", amount = warrantyAmount)
            }
        }
    }
}

@Composable
fun DonutChart(
    receiptRatio: Float,
    modifier: Modifier = Modifier
) {
    val animatedRatio = remember { Animatable(0f) }
    LaunchedEffect(receiptRatio) {
        animatedRatio.animateTo(receiptRatio, animationSpec = tween(durationMillis = 1000))
    }

    Canvas(modifier = modifier) {
        val strokeWidth = 12.dp.toPx()
        val radius = size.minDimension / 2
        
        // Background Circle (Warranties Color)
        drawCircle(
            color = SecondaryColor.copy(alpha = 0.2f),
            style = Stroke(width = strokeWidth)
        )
        
        // Foreground Arc (Receipts Color) - Actually we should draw 2 arcs for accuracy
        // Arc 1: Warranty (Remaining)
        drawArc(
            color = SecondaryColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Arc 2: Receipt (Animated)
        drawArc(
            color = BrandColor,
            startAngle = -90f,
            sweepAngle = 360f * animatedRatio.value,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String, amount: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$${String.format("%.2f", amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ModernStatCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape)
                    .padding(6.dp)
            ) {
                 Icon(
                     imageVector = if (title.contains("Month")) Icons.Default.ShoppingCart else Icons.Default.Info,
                     contentDescription = null,
                     tint = color,
                     modifier = Modifier.fillMaxSize()
                 )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "$${String.format("%.0f", amount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RecentItemRow(name: String, date: String, price: Double, type: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Medium)
            Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            "$${String.format("%.2f", price)}",
            fontWeight = FontWeight.Bold,
            color = if (type == "RECEIPT") BrandColor else SecondaryColor
        )
    }
}
