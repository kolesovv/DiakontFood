package com.github.kolesovv.diakontfood.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.usecase.GetDishesUseCase
import com.github.kolesovv.diakontfood.domain.usecase.GetOrdersUseCase
import com.github.kolesovv.diakontfood.domain.usecase.RegisterOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    val getDishesUseCase: GetDishesUseCase,
    val registerOrderUseCase: RegisterOrderUseCase,
    val getOrderUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _state = MutableStateFlow<OrderState>(OrderState.Initialization)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadDishes()
        }
    }

    private fun loadDishes() {
        viewModelScope.launch {
            getDishesUseCase().collect {
                _state.value = OrderState.WaitingForDishSelection(dishes = it)
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            getOrderUseCase().collect {
                _orders.value = it
            }
        }
    }

    fun processCommand(command: OrderCommands) {
        when (command) {
            OrderCommands.RefreshData -> {
                loadDishes()
            }

            is OrderCommands.RegisterOrder -> {
                viewModelScope.launch {
                    registerOrderUseCase(command.dishId, command.rfid)
                    _state.value = OrderState.Initialization
                    loadDishes()
                    loadOrders()
                }
            }

            is OrderCommands.ScanRFID -> {
                _state.update { previousState ->
                    if (previousState is OrderState.WaitingForRfid) {
                        val scannedRfid = command.rfid
                        OrderState.OrderRegistered(
                            dishId = previousState.selectedDishId,
                            rfid = scannedRfid
                        )
                    } else {
                        previousState
                    }
                }
            }

            is OrderCommands.SelectDish -> {
                _state.update { previousState ->
                    if (previousState is OrderState.WaitingForDishSelection) {
                        OrderState.WaitingForRfid(
                            dishes = previousState.dishes,
                            selectedDishId = command.dishId
                        )
                    } else {
                        previousState
                    }
                }
            }
        }
    }
}

sealed interface OrderCommands {

    data class SelectDish(val dishId: Int) : OrderCommands
    data class ScanRFID(val rfid: String) : OrderCommands
    data class RegisterOrder(val dishId: Int, val rfid: String) : OrderCommands
    data object RefreshData : OrderCommands
}

sealed interface OrderState {
    object Initialization : OrderState
    data class WaitingForDishSelection(
        val dishes: List<Dish>,
        val selectedDishId: Int? = null
    ) : OrderState

    data class WaitingForRfid(
        val dishes: List<Dish>,
        val selectedDishId: Int,
        val scannedRfid: String? = null
    ) : OrderState

    data class OrderRegistered(
        val dishId: Int,
        val rfid: String
    ) : OrderState
}
