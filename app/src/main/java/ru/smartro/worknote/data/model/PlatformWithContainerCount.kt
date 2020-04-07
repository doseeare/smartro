package ru.smartro.worknote.data.model

data class PlatformWithContainerCount(
    val id: Int,
    val name: String,
    val address: String,
    val containersCount: Int,
    val latitude: Double,
    val longitude: Double
)