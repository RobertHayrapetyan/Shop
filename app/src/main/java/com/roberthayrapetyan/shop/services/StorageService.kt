package com.roberthayrapetyan.shop.services

import com.roberthayrapetyan.shop.data.entity.OrderedItem
import com.roberthayrapetyan.shop.data.entity.Product
import com.roberthayrapetyan.shop.data.entity.Profile
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import javax.inject.Inject

class StorageService @Inject constructor(boxStore: BoxStore) {

    private val profileBox: Box<Profile> = boxStore.boxFor()
    private val cartBox: Box<Product> = boxStore.boxFor()
    private val orderBox: Box<OrderedItem> = boxStore.boxFor()

    fun getProfile(): Profile?{
        return if (profileBox.all.isEmpty()){
            null
        }else{
            profileBox.all.first()
        }
    }
    fun setProfile(profile: Profile) {
        profileBox.removeAll()
        profileBox.put(profile)
    }

    fun addToCart(item: Product) {
        if (cartBox.all.find {  it.id == item.id } != null) {
            val oldItem = cartBox.all.find {  it.id == item.id }
            oldItem?.copy(count = oldItem.count + item.count)?.let {
                cartBox.all.remove(item)
                cartBox.put(it)
            }
        } else {
            cartBox.put(item)
        }
    }

    fun getCart(): List<Product>? {
        return cartBox.all
    }

    fun removeFromCart(product: Product) {
        val cartList = cartBox.all
        cartBox.removeAll()
        cartList.remove(product)
        cartBox.put(cartList)
    }

    fun updateProfileBalance(balance: Float?) {
        val updatedProfile = profileBox.all.first().copy(balance = balance)
        profileBox.removeAll()
        profileBox.put(updatedProfile)
    }

    fun addOrder(orderItems: List<Product>) {
        val orderList = orderItems.map {
            OrderedItem(
                id = it.id,
                name = it.name,
                price = it.price * it.count,
                image = it.image,
                count = it.count
            )
        }
        orderBox.put(orderList)
        cartBox.removeAll()
    }

    fun updateCartItemCount(product: Product, count: Int) {
        val cartList = cartBox.all
        cartList.find { it.id == product.id }?.let {
            if (it.count == 1L && count == -1){
                cartList.remove(product)
            }else{
                it.count += count
            }
        } ?: return
        cartBox.removeAll()
        cartBox.put(cartList)
    }
}