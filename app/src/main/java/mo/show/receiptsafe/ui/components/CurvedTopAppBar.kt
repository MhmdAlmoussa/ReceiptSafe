package mo.show.receiptsafe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import mo.show.receiptsafe.ui.theme.BrandColor
import mo.show.receiptsafe.ui.theme.ReceiptTeal

@Composable
fun CurvedTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp) // Taller to accommodate curve
            .graphicsLayer {
                shape = CurvedBottomShape()
                clip = true
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BrandColor,
                        ReceiptTeal
                    )
                )
            )
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp) // Push text up slightly
        )
        
        // Actions Row
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
    }
}

class CurvedBottomShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - 40f) // End straight line a bit before bottom
            quadraticBezierTo(
                size.width / 2, size.height + 40f, // Control point below center
                0f, size.height - 40f // End point
            )
            close()
        }
        return Outline.Generic(path)
    }
}
