package com.github.kolesovv.diakontfood.presentation.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.kolesovv.diakontfood.R
import com.github.kolesovv.diakontfood.domain.entity.Dish
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: MenuViewModel = hiltViewModel(),
    onNavigateToPayment: (List<Int>) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val dishesState by viewModel.dishes.collectAsState()
    val selectedIds by viewModel.selectedDishIds.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.menu))
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 14.dp),
                        onClick = {
                            viewModel.processCommand(MenuCommands.UpdateMenu)
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )
                        }
                    )
                },
                actions = {
                    IconButton(
                        modifier = Modifier.padding(end = 14.dp),
                        onClick = onNavigateToHistory
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = { onNavigateToPayment(selectedIds) },
                    enabled = selectedIds.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(stringResource(R.string.proceed_to_payment))
                }
            }
        }
    ) { paddingValues ->
        when (val state = dishesState) {
            is DishState.Initial, is DishState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DishState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.processCommand(MenuCommands.UpdateMenu)
                        }) {
                            Text(stringResource(R.string.repeat))
                        }
                    }
                }
            }

            is DishState.Selection -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(state.dishes) { dish ->
                        DishItem(
                            modifier = Modifier.padding(start = 14.dp),
                            dish = dish,
                            checked = dish.dishId in selectedIds,
                            onCheckedChange = {
                                viewModel.processCommand(MenuCommands.ToggleDishSelection(dish.dishId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DishItem(
    modifier: Modifier,
    dish: Dish,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(dish.name) },
        supportingContent = { Text("${dish.price} ₽") },
        trailingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
    HorizontalDivider()
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
