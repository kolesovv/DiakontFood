package com.github.kolesovv.diakontfood.presentation.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.usecase.GetDishesUseCase
import com.github.kolesovv.diakontfood.domain.usecase.UpdateDishesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val updateDishesUseCase: UpdateDishesUseCase,
    private val getDishesUseCase: GetDishesUseCase
) : ViewModel() {

    private val _dishes = MutableStateFlow<DishState>(DishState.Initial)
    val dishes = _dishes.asStateFlow()

    private val _selectedDishIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedDishIds = _selectedDishIds.asStateFlow()

    init {
        observeDishes()
    }

    private fun observeDishes() {
        viewModelScope.launch {
            getDishesUseCase().collect { dishes ->
                _dishes.update {
                    DishState.Selection(dishes)
                }
            }
        }
    }

    fun processCommand(command: MenuCommands) {
        when (command) {
            MenuCommands.UpdateMenu -> updateMenu()
            is MenuCommands.ToggleDishSelection -> toggleSelection(command.dishId)
            MenuCommands.ClearSelection -> clearSelection()
        }
    }

    private fun updateMenu() {
        viewModelScope.launch {
            _dishes.value = DishState.Loading
            try {
                updateDishesUseCase()
            } catch (e: Exception) {
                _dishes.value = DishState.Error("Не удалось загрузить меню")
            }
        }
    }

    private fun toggleSelection(id: Int) {
        _selectedDishIds.update { list ->
            if (id in list) {
                list - id
            } else {
                list + id
            }
        }
    }

    private fun clearSelection() {
        _selectedDishIds.value = emptyList()
    }
}

sealed interface MenuCommands {

    data object UpdateMenu : MenuCommands
    data class ToggleDishSelection(val dishId: Int) : MenuCommands
    data object ClearSelection : MenuCommands
}

sealed interface DishState {
    object Initial : DishState
    object Loading : DishState
    data class Selection(val dishes: List<Dish>) : DishState
    data class Error(val message: String) : DishState
}
