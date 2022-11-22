package com.roberthayrapetyan.shop.data.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Profile(
    @Id
    var _id: Long = 0,
    val id: Int?,
    val name: String?,
    val balance: Float?,
    val image: String?
)
