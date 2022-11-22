package com.roberthayrapetyan.shop.ui.viewmodels

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
class ProductListScreenViewModel @Inject constructor(
    private val storageService: StorageService
) : ViewModel() {
    val productList = ArrayList<Product>()

    private val _profile = MutableLiveData<Profile>(storageService.getProfile())
    val profile: LiveData<Profile> = _profile

    private val _cartItemCount = MutableLiveData(storageService.getCart()?.size ?: 0)
    val cartItemCount: LiveData<Int> = _cartItemCount

    private val _cartTotalPrice = MutableLiveData(storageService.getCart()?.map { it.count * it.price }?.sum())
    val cartTotalPrice: LiveData<Float?> = _cartTotalPrice

    init {
        onResume()
    }

    fun onResume() {
        getProfile()
        getProductList()
        getCartItemCount()
        getCartTotalPrice()
    }

    private fun getCartTotalPrice() {
        _cartTotalPrice.value = storageService.getCart()?.map { it.count * it.price }?.sum()
    }

    private fun getCartItemCount() {
        _cartItemCount.value = storageService.getCart()?.sumOf { it.count.toInt() } ?: 0
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

    private fun getProductList() {
        productList.clear()
        productList.addAll(
            listOf(
                Product(
                    id = 0,
                    name = "Iphone 13 Pro",
                    price = 345000f,
                    image = "https://allcell.am/wp-content/uploads/2021/09/iphone-13pro.jpg"
                ),
                Product(
                    id = 1,
                    name = "Iphone 11 Pro",
                    price = 315500f,
                    image = "https://allcell.am/wp-content/uploads/2020/05/iphone-11-pro-silver-select-2019_GEO_EMEA-%D0%BA%D0%BE%D0%BF%D0%B8%D1%8F.png"
                ),
                Product(
                    id = 2,
                    name = "Samsung A51",
                    price = 123000f,
                    image = "https://www.zigzag.am/media/catalog/product/cache/811d9bdbaebf1cf745388b9849057259/3/4/3471201.jpg"
                ),
                Product(
                    id = 3,
                    name = "Samsung Note 10",
                    price = 230000f,
                    image = "https://m.media-amazon.com/images/I/419VTYVRD1L._AC_.jpg"
                ),
                Product(
                    id = 4,
                    name = "Samsung S22",
                    price = 287500f,
                    image = "https://m.media-amazon.com/images/I/71qZERyxy6L._SX679_.jpg"
                ),
                Product(
                    id = 5,
                    name = "Nokia 3310",
                    price = 55000f,
                    image = "https://vega.am/image/cache/catalog/1HRACH/2020/April/NOKIA%203310%20TA-1006%20(CHARCOAL)-2000x1500.jpg"
                ),
                Product(
                    id = 6,
                    name = "Nokia G",
                    price = 98000f,
                    image = "https://images.ctfassets.net/wcfotm6rrl7u/3L3nuuk4Sf3ba3cBsoIMMd/7a1cde5a755b87809a5c33ae837cac6f/nokia-X20-midnight_sun-front_back-int.png"
                ),
                Product(
                    id = 7,
                    name = "Nokia 6",
                    price = 83000f,
                    image = "https://imei.org/storage/files/images/5785/preview/nokia-6-2.png"
                ),
                Product(
                    id = 8,
                    name = "Alcatel One X",
                    price = 78000f,
                    image = "https://ru.gecid.com/data/sphone/201408140905-32962/img/mini-00_alcatel_onetouch_idol_x.jpg"
                ),
            )
        )
    }

    fun addToCart(product: Product, count: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            storageService.addToCart(product.copy(count = count))
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                _cartItemCount.value = storageService.getCart()?.sumOf { it.count.toInt() } ?: 0
                _cartTotalPrice.value = storageService.getCart()?.map { it.count * it.price }?.sum()
            }
        }
    }
}