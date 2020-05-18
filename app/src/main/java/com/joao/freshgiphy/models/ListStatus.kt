package com.joao.freshgiphy.models

enum class Status {
    DEFAULT, LOADING, EMPTY, ERROR
}

data class ListStatus(
    val status: Status,
    val message: String? = null
)