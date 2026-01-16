package mo.show.receiptsafe.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mo.show.receiptsafe.ui.AppViewModelProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onBackClick: () -> Unit,
    onDeleteSuccess: () -> Unit,
    viewModel: ProductDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val product = viewModel.product

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = { viewModel.deleteProduct(onDeleteSuccess) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (product != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Type Badge
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (product.type == "WARRANTY") mo.show.receiptsafe.ui.theme.BrandColor else mo.show.receiptsafe.ui.theme.ReceiptTeal,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ) {
                            Text(
                                text = product.type,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Purchase Date", style = MaterialTheme.typography.labelSmall)
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        Text(
                            dateFormat.format(Date(product.purchaseDate)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        if (product.type == "WARRANTY") {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("Warranty Duration", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "${product.warrantyDurationMonths} Months",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("Status", style = MaterialTheme.typography.labelSmall)
                            val statusText = if (product.isExpired) "Expired" else "Active"
                            val statusColor = if (product.isExpired) mo.show.receiptsafe.ui.theme.ExpiredRed else mo.show.receiptsafe.ui.theme.ActiveGreen
                            Text(statusText, color = statusColor, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                // Receipt Image
                if (product.receiptImagePath != null) {
                    Text("Receipt", style = MaterialTheme.typography.titleMedium)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = File(product.receiptImagePath),
                            contentDescription = "Receipt Image",
                            modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                } else {
                    Text("No receipt image attached.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
