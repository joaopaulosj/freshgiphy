package com.joao.freshgiphy.models

enum class Status {
    SUCCESS, LOADING, EMPTY, ERROR
}

data class ListStatus(
    val status: Status,
    val message: String? = null
)