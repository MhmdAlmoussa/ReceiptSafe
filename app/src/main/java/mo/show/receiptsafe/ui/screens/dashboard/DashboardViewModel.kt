package mo.show.receiptsafe.ui.screens.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mo.show.receiptsafe.domain.model.Product
import mo.show.receiptsafe.domain.usecase.GetProductsUseCase

class DashboardViewModel(
    getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    var selectedFilter by mutableStateOf(FilterType.ALL)
        private set

    fun updateFilter(filter: FilterType) {
        selectedFilter = filter
    }

    val uiState: StateFlow<DashboardUiState> = getProductsUseCase()
        .map { products ->
            products.sortedByDescending { it.purchaseDate }
        }.combine(snapshotFlow { selectedFilter }) { products, filter ->
            val filtered = when (filter) {
                FilterType.ALL -> products
                FilterType.RECEIPT -> products.filter { it.type == "RECEIPT" }
                FilterType.WARRANTY -> products.filter { it.type == "WARRANTY" }
            }
            
            // Calculate Stats
            val calendar = java.util.Calendar.getInstance()
            val currentYear = calendar.get(java.util.Calendar.YEAR)
            val currentMonth = calendar.get(java.util.Calendar.MONTH)
            
            val totalSpent = products.sumOf { it.price }
            val yearlySpent = products.filter { 
                calendar.timeInMillis = it.purchaseDate
                calendar.get(java.util.Calendar.YEAR) == currentYear
            }.sumOf { it.price }
            
            val monthlySpent = products.filter {
                calendar.timeInMillis = it.purchaseDate
                calendar.get(java.util.Calendar.YEAR) == currentYear &&
                calendar.get(java.util.Calendar.MONTH) == currentMonth
            }.sumOf { it.price }

            val spentOnReceipts = products.filter { it.type == "RECEIPT" }.sumOf { it.price }
            val spentOnWarranties = products.filter { it.type == "WARRANTY" }.sumOf { it.price }

            DashboardUiState(
                items = filtered,
                totalSpent = totalSpent,
                spentThisYear = yearlySpent,
                spentThisMonth = monthlySpent,
                spentOnReceipts = spentOnReceipts,
                spentOnWarranties = spentOnWarranties
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )
}

enum class FilterType { ALL, RECEIPT, WARRANTY }

data class DashboardUiState(
    val items: List<Product> = emptyList(),
    val totalSpent: Double = 0.0,
    val spentThisYear: Double = 0.0,
    val spentThisMonth: Double = 0.0,
    val spentOnReceipts: Double = 0.0,
    val spentOnWarranties: Double = 0.0
)
