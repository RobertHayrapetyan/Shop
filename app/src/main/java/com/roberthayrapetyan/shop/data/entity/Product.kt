package com.roberthayrapetyan.shop.data.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Product(
    @Id
    var _id: Long = 0,
    val id: Int,
    val name: String,
    val price: Float,
    val image: String,
    var count: Long = 0
)
