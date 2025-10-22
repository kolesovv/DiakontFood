package com.github.kolesovv.diakontfood.presentation.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kolesovv.diakontfood.domain.usecase.PayMethod
import com.github.kolesovv.diakontfood.domain.usecase.RegisterOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val registerOrderUseCase: RegisterOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PaymentState>(PaymentState.Initial(emptyList()))
    val state = _state.asStateFlow()

    private val _cardNumber = MutableStateFlow("")
    val cardNumber = _cardNumber.asStateFlow()

    private var currentDishIds: List<Int> = emptyList()

    fun initPayment(dishIds: List<Int>) {
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
                val response = registerOrderUseCase(
                    dishIds = currentDishIds,
                    payMethod = payMethod,
                    cardNumber = if (payMethod == PayMethod.CARD) _cardNumber.value else EMPTY_CARD_NUMBER
                )
                /*when (response.resultCode) {
                    OK -> _state.value = PaymentState.Success
                    CARD_BLOCKED -> _state.value = PaymentState.Error("Карта заблокирована")
                    CARD_NOT_FOUND -> _state.value = PaymentState.Error("Карта не найдена")
                    USER_BLOCKED -> _state.value = PaymentState.Error("Сотрудник заблокирован")
                    else -> _state.value = PaymentState.Error("Ошибка при оплате")
                }*/
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