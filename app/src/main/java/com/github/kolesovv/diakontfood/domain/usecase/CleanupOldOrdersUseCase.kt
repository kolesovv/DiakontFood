package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import javax.inject.Inject

class CleanupOldOrdersUseCase @Inject constructor(
    private val diakontFoodRepository: DiakontFoodRepository
) {

    suspend operator fun invoke() {
        diakontFoodRepository.cleanupOldOrders()
    }
}
