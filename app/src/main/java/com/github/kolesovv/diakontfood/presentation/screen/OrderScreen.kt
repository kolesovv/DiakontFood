package com.github.kolesovv.diakontfood.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.kolesovv.diakontfood.domain.entity.Dish
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun OrderScreen(
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value

    val orders = viewModel.orders.collectAsState()
    val currentOrders = orders.value

    Scaffold { innerPadding ->
        when (currentState) {
            OrderState.Initialization -> {
                LaunchedEffect(Unit) {
                    viewModel.processCommand(OrderCommands.RefreshData)
                }
            }

            is OrderState.OrderRegistered -> {
                LaunchedEffect(currentState) {
                    viewModel.processCommand(
                        OrderCommands.RegisterOrder(
                            currentState.dishId,
                            currentState.rfid
                        )
                    )
                }
            }

            is OrderState.WaitingForDishSelection -> {
                Column {
                    Menu(
                        modifier = modifier.padding(innerPadding),
                        dishes = currentState.dishes,
                        onDishClick = {
                            viewModel.processCommand(OrderCommands.SelectDish(it))
                        }
                    )
                    LazyColumn(
                        modifier = modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (!currentOrders.isEmpty()) {
                            item {
                                Text(
                                    modifier = modifier,
                                    text = "История заказов:"
                                )
                            }
                            items(currentOrders) { order ->
                                Text(
                                    text = "ID Обеда: ${order.dishId} : RFID: ${order.cardNumber}",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            is OrderState.WaitingForRfid -> {
                RfidScanner(
                    modifier = modifier.padding(innerPadding),
                    dishId = currentState.selectedDishId,
                    onRfidChange = {
                        viewModel.processCommand(OrderCommands.ScanRFID(it))
                    }
                )
            }
        }
    }
}

@Composable
fun Menu(
    modifier: Modifier,
    dishes: List<Dish>,
    onDishClick: (dishId: Int) -> Unit
) {

    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(dishes) { dish ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                onClick = { onDishClick(dish.dishId) }
            ) {
                Text(
                    text = "${dish.name}, ${dish.price} ₽",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun RfidScanner(
    modifier: Modifier,
    dishId: Int,
    onRfidChange: (rfid: String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val status = remember { mutableStateOf("Приложите карту для оплаты обеда $dishId...") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(text) {
        if (text.endsWith("\n")) {
            val rfid = text.trim()
            status.value = "Карта принята!\n${rfid}"
            delay(2000)
            onRfidChange(rfid)
            focusManager.clearFocus()
        }
    }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .alpha(0f),
        value = text,
        singleLine = true,
        readOnly = true,
        onValueChange = { },
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = status.value,
            fontSize = 24.sp
        )
    }

    // Симуляция ввода RFID
    LaunchedEffect(Unit) {
        delay(2000)
        val generatedRfid = Random.nextInt(10_000_000, 99_999_999)
        text = "$generatedRfid\n"
    }
}
