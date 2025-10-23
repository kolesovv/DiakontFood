package com.github.kolesovv.diakontfood.presentation.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.usecase.CleanupOldOrdersUseCase
import com.github.kolesovv.diakontfood.domain.usecase.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val cleanupOldOrdersUseCase: CleanupOldOrdersUseCase
) : ViewModel() {

    private val _orders = MutableStateFlow<OrderState>(OrderState.Initial)
    val orders = _orders.asStateFlow()


    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            cleanupOldOrdersUseCase()
            getOrdersUseCase().collect { orders ->
                _orders.update {
                    OrderState.Overview(orders)
                }
            }
        }
    }
}

sealed interface OrderState {
    object Initial : OrderState
    data class Overview(val orders: List<Order>) : OrderState
}
