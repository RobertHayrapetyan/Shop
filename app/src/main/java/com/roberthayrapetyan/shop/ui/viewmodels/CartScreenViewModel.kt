package com.roberthayrapetyan.shop.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roberthayrapetyan.shop.data.entity.Product
import com.roberthayrapetyan.shop.data.entity.Profile
import com.roberthayrapetyan.shop.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartScreenViewModel @Inject constructor(private val storageService: StorageService): ViewModel() {
    var cartItems by mutableStateOf(storageService.getCart())
    var orderSum by mutableStateOf(cartItems?.map { it.count * it.price }?.sum())

    private val _profile = MutableLiveData<Profile>(storageService.getProfile())
    val profile: LiveData<Profile> = _profile

    private val _orderEnabled = MutableLiveData(profile.value?.balance?.compareTo(orderSum ?: 0f)!! > 0 && orderSum != 0f)
    val orderEnabled: LiveData<Boolean> = _orderEnabled

    private val _cartTotalPrice = MutableLiveData(storageService.getCart()?.map { it.count * it.price }?.sum())
    val cartTotalPrice: LiveData<Float?> = _cartTotalPrice

    init {
        onUpdate()
    }

    fun onResume(){
        onUpdate()
    }

    private fun onUpdate() {
        getProfile()
        getCartList()
        _orderEnabled.value = profile.value?.balance?.compareTo(orderSum ?: 0f)!! > 0 && orderSum != 0f
        _cartTotalPrice.value = storageService.getCart()?.map { it.count * it.price }?.sum()
    }

    private fun getCartList() {
        cartItems = storageService.getCart()
        orderSum = cartItems?.map { it.count * it.price }?.sum()
    }

    fun removeFromCart(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            storageService.removeFromCart(product)
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                onUpdate()
            }
        }
    }

    private fun getProfile() {
        _profile.value = if (profile.value == null) {
            storageService.setProfile(
                profile = Profile(
                    id = 0,
                    name = "Robert",
                    balance = 1789000f,
                    image = "https://www.w3schools.com/howto/img_avatar.png"
                )
            )
            storageService.getProfile()
        } else {
            storageService.getProfile()
        }
    }

    fun checkout() {
        viewModelScope.launch(Dispatchers.IO) {
            storageService.updateProfileBalance(orderSum?.let { _profile.value?.balance?.minus(it) })
            cartItems?.let { storageService.addOrder(it) }
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                onUpdate()
            }
        }
    }
}