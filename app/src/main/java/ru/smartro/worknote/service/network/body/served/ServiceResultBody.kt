package ru.smartro.worknote.service.network.body.served


import com.google.gson.annotations.SerializedName


data class ServiceResultBody(
    @SerializedName("ps")
    val ps: List<ContainerPointServed>
)