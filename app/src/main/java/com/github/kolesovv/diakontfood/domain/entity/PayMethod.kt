package com.github.kolesovv.diakontfood.domain.entity

enum class PayMethod(val code: Int) {
    GUEST(0),
    CARD(1),
    NO_CARD(-1)
}
