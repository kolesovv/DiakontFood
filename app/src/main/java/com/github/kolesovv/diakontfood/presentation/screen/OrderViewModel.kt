package com.github.kolesovv.diakontfood.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.usecase.GetDishesUseCase
import com.github.kolesovv.diakontfood.domain.usecase.RegisterOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
data class OrderViewModel @Inject constructor(
    val getDishesUseCase: GetDishesUseCase,
    val registerOrderUseCase: RegisterOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getDishesUseCase().collect {
                _state.value = OrderState().copy(dishes = it)
            }
        }
    }

    fun processCommand(command: OrderCommands) {
        when (command) {
            is OrderCommands.ChooseDish -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        previousState.copy(dishId = command.dishId)
                    }
                }
            }

            is OrderCommands.ScanRFID -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        previousState.copy(rfid = command.rfid)
                    }
                }
            }

            is OrderCommands.RegisterOrder -> {
                viewModelScope.launch {
                    registerOrderUseCase(
                        dishId = command.dishId,
                        rfid = command.rfid
                    )
                }
            }

            OrderCommands.RefreshData -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        previousState.copy(
                            dishId = OrderState.DISH_ID_UNSPECIFIED,
                            rfid = OrderState.RFID_UNSPECIFIED
                        )
                    }
                }
            }
        }
    }
}

sealed interface OrderCommands {

    data class ChooseDish(val dishId: Int) : OrderCommands
    data class ScanRFID(val rfid: String) : OrderCommands
    data class RegisterOrder(val dishId: Int, val rfid: String) : OrderCommands
    data object RefreshData : OrderCommands
}

data class OrderState(
    val dishes: List<Dish> = listOf(),
    val dishId: Int = DISH_ID_UNSPECIFIED,
    val rfid: String = RFID_UNSPECIFIED
) {
    companion object {
        const val DISH_ID_UNSPECIFIED = -1
        const val RFID_UNSPECIFIED = ""
    }
}