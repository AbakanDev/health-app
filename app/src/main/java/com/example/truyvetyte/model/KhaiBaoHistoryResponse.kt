package com.example.truyvetyte.model

data class KhaiBaoHistoryResponse(
    val success: Boolean,
    val message: String,
    val totalDeclarations: Int,
    val data: List<KhaiBaoHistoryItem>?
)

data class KhaiBaoHistoryItem(
    val LanKhaiBao: Int,
    val Ngay: String,
    val Gio: String
)