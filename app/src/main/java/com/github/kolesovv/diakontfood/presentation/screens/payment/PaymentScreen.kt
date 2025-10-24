package com.github.kolesovv.diakontfood.presentation.screens.payment

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.kolesovv.diakontfood.R
import com.github.kolesovv.diakontfood.domain.entity.PayMethod
import com.github.kolesovv.diakontfood.presentation.components.DisableSoftKeyboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    dishIds: List<Int>,
    viewModel: PaymentViewModel = hiltViewModel(
        creationCallback = { factory: PaymentViewModel.Factory ->
            factory.create(dishIds)
        }
    ),
    onBackToMenu: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val cardNumber by viewModel.cardNumber.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.Pay))
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 14.dp),
                        onClick = onBackToMenu
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.Back
                            )
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                is PaymentState.Initial -> {
                    PaymentContent(
                        dishIds = s.dishIds,
                        cardNumber = cardNumber,
                        onCardNumberChange = {
                            viewModel.processCommand(PaymentCommands.EnterCardNumber(it))
                        },
                        onClearCardNumber = {
                            viewModel.processCommand(PaymentCommands.ClearCardNumber)
                        },
                        onPayGuest = {
                            viewModel.processCommand(PaymentCommands.Pay(PayMethod.GUEST))
                        },
                        onPayNoCard = {
                            viewModel.processCommand(PaymentCommands.Pay(PayMethod.NO_CARD))
                        },
                        onPayCard = {
                            viewModel.processCommand(PaymentCommands.Pay(PayMethod.CARD))
                        }
                    )
                }

                PaymentState.Loading -> CircularProgressIndicator()

                is PaymentState.Error -> {
                    PaymentError(
                        message = s.message,
                        onRetry = { viewModel.returnToSelection() }
                    )
                }

                PaymentState.Success -> {
                    PaymentSuccess(onBackToMenu = onBackToMenu)
                }
            }
        }
    }
}

@Composable
fun PaymentContent(
    dishIds: List<Int>,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    onClearCardNumber: () -> Unit,
    onPayGuest: () -> Unit,
    onPayNoCard: () -> Unit,
    onPayCard: () -> Unit
) {
    val isCardEntered = cardNumber.length == 10
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text(stringResource(R.string.selected_positions, dishIds.size))

        DisableSoftKeyboard {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .focusRequester(focusRequester),
                value = cardNumber,
                onValueChange = {
                    if (it.length <= 10) {
                        onCardNumberChange(it)
                    }
                },
                label = { Text(stringResource(R.string.card_number)) },
                singleLine = true,
                trailingIcon = {
                    if (cardNumber.isNotEmpty()) {
                        IconButton(onClick = onClearCardNumber) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = stringResource(R.string.Clear)
                            )
                        }
                    }
                }
            )
        }

        Button(
            onClick = onPayCard,
            enabled = isCardEntered,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.pay_by_card))
        }

        OutlinedButton(
            onClick = onPayGuest,
            enabled = !isCardEntered,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.pay_as_a_guest))
        }

        OutlinedButton(
            onClick = onPayNoCard,
            enabled = !isCardEntered,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.pay_without_a_card))
        }
    }
}

@Composable
fun PaymentError(
    message: String,
    onRetry: () -> Unit
) {
    Toast.makeText(
        LocalContext.current,
        message,
        Toast.LENGTH_SHORT
    ).show()
    onRetry()
}

@Composable
fun PaymentSuccess(
    onBackToMenu: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )

            Text(
                text = stringResource(R.string.successful_payment),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.back_to_menu))
            }
        }
    }
}
