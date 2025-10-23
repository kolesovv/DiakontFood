package com.github.kolesovv.diakontfood.presentation.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kolesovv.diakontfood.domain.entity.OrderStatus
import com.github.kolesovv.diakontfood.domain.entity.PayMethod
import com.github.kolesovv.diakontfood.domain.repository.OrderResponse
import com.github.kolesovv.diakontfood.domain.usecase.RegisterOrderUseCase
import com.github.kolesovv.diakontfood.domain.usecase.SaveOrderUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PaymentViewModel.Factory::class)
class PaymentViewModel @AssistedInject constructor(
    @Assisted("dishIds") private val dishIds: List<Int>,
    private val registerOrderUseCase: RegisterOrderUseCase,
    private val saveOrderUseCase: SaveOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PaymentState>(PaymentState.Initial(emptyList()))
    val state = _state.asStateFlow()

    private val _cardNumber = MutableStateFlow("")
    val cardNumber = _cardNumber.asStateFlow()

    private var currentDishIds: List<Int> = emptyList()

    init {
        initPayment(dishIds)
    }

    private fun initPayment(dishIds: List<Int>) {
        currentDishIds = dishIds
        _state.value = PaymentState.Initial(dishIds)
    }

    fun processCommand(command: PaymentCommands) {
        when (command) {
            is PaymentCommands.EnterCardNumber -> {
                _cardNumber.update { command.value.take(10) }
            }

            is PaymentCommands.Pay -> pay(command.payMethod)

            PaymentCommands.ClearCardNumber -> _cardNumber.value = EMPTY_CARD_NUMBER
        }
    }

    private fun pay(payMethod: PayMethod) {
        viewModelScope.launch {
            _state.value = PaymentState.Loading
            try {
                val responses = registerOrderUseCase(
                    dishIds = currentDishIds,
                    payMethod = payMethod,
                    cardNumber = when (payMethod) {
                        PayMethod.GUEST -> {
                            payMethod.code.toString()
                        }

                        PayMethod.NO_CARD -> {
                            payMethod.code.toString()
                        }

                        else -> {
                            _cardNumber.value
                        }
                    }
                )
                responses.forEach { orderResponse ->
                    when (orderResponse) {
                        is OrderResponse.Error -> PaymentState.Error(orderResponse.message)
                        is OrderResponse.Success<OrderStatus> -> {
                            when (orderResponse.data.code) {
                                1 -> {
                                    currentDishIds.forEach {
                                        saveOrderUseCase(it, orderResponse.data.cardNumber)
                                    }
                                    _state.value = PaymentState.Success
                                }

                                0 -> PaymentState.Error("Ошибка выполнения: ${orderResponse.data.message}")
                                2 -> PaymentState.Error("Карта не зарегестрирована в системе")
                                3 -> PaymentState.Error("Карта заблокирована")
                                4 -> PaymentState.Error("Работник заблокирован")
                                else -> PaymentState.Error("Неизвестный ответ сервера: ${orderResponse.data.code}")
                            }
                        }
                    }
                }
                _state.value = PaymentState.Success
            } catch (e: Exception) {
                _state.value = PaymentState.Error("Ошибка сети")
            }
        }
    }

    fun returnToSelection() {
        _state.value = PaymentState.Initial(currentDishIds)
    }

    companion object {
        const val EMPTY_CARD_NUMBER = ""
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("dishIds") dishIds: List<Int>
        ): PaymentViewModel
    }
}

sealed interface PaymentCommands {
    data class EnterCardNumber(val value: String) : PaymentCommands
    data class Pay(val payMethod: PayMethod) : PaymentCommands
    data object ClearCardNumber : PaymentCommands
}

sealed interface PaymentState {
    data class Initial(val dishIds: List<Int>) : PaymentState
    data object Loading : PaymentState
    data object Success : PaymentState
    data class Error(val message: String) : PaymentState
}