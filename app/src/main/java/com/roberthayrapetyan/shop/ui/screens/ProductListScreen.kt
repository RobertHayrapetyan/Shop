package com.roberthayrapetyan.shop.ui.screens

import android.icu.text.NumberFormat
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.roberthayrapetyan.shop.R
import com.roberthayrapetyan.shop.data.entity.Product
import com.roberthayrapetyan.shop.data.entity.Profile
import com.roberthayrapetyan.shop.ui.navigation.NavigationItem
import com.roberthayrapetyan.shop.ui.theme.*
import com.roberthayrapetyan.shop.ui.viewmodels.ProductListScreenViewModel
import java.util.*

@Preview
@Composable
fun ProductListScreen(
    viewModel: ProductListScreenViewModel? = null, navController: NavHostController? = null
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val profile = viewModel?.profile?.observeAsState()
    val context = LocalContext.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel?.onResume()
                }
                else -> {}
            }
        }
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val cartItemCount = viewModel?.cartItemCount?.observeAsState()
    val cartTotalPrice = viewModel?.cartTotalPrice?.observeAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(), topBar = {
            ItemToolbar(profile = profile?.value, cartItemCount = cartItemCount, cartTotalPrice = cartTotalPrice?.value ?: 0f, onCartClicked = {
                navController?.navigate(NavigationItem.CartScreen.route)
            })
        }, backgroundColor = background
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(viewModel?.productList ?: emptyList()) { product ->
                val toastSuccess = stringResource(id = R.string.label_added_to_cart)
                val toastFailure = stringResource(id = R.string.label_insufficient_balance)
                ItemProduct(product = product, onAddToCartClicked = { count->
                    if (cartTotalPrice?.value?.plus(product.price * count)?.compareTo(profile?.value?.balance ?: 0f)!! <= 0){
                        viewModel.addToCart(product, count)
                        Toast.makeText(context, toastSuccess, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, toastFailure, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}

@Composable
fun ItemToolbar(
    modifier: Modifier = Modifier,
    profile: Profile?,
    cartItemCount: State<Int?>?,
    onCartClicked: () -> Unit,
    cartTotalPrice: Float
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(topBarBackground)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.app_name),
            color = textLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Image(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            painter = rememberAsyncImagePainter(profile?.image ?: ""),
            contentDescription = null
        )
        Column(
            modifier = Modifier.padding(start = 10.dp), verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = profile?.name ?: "Name", color = textLight, fontWeight = FontWeight.Bold
            )
            Text(
                text = "${
                    NumberFormat.getInstance(Locale.US).format(profile?.balance?.minus(cartTotalPrice) ?: 0f)
                } դր.", color = textLight, fontWeight = FontWeight.Bold, fontSize = 14.sp
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .clip(CircleShape)
                .clickable(interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = rippleLight),
                    onClick = {
                        onCartClicked()
                    })
        ) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = R.drawable.ic_cart),
                contentDescription = null,
                tint = textLight
            )
            if (cartItemCount?.value != 0) {
                Text(
                    modifier = Modifier
                        .padding(top = 5.dp, end = 5.dp)
                        .size(14.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color.Red),
                    text = cartItemCount?.value.toString(),
                    color = textLight,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ItemProduct(
    modifier: Modifier = Modifier,
    product: Product,
    onAddToCartClicked: (Long)->Unit
) {
    var count by remember { mutableStateOf(1L) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 5.dp,
        backgroundColor = itemBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(color = Color.Gray, width = 1.dp, shape = RoundedCornerShape(10.dp))
                    .background(Color.Gray),
                painter = rememberAsyncImagePainter(product.image),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(
                    text = product.name,
                    color = textDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.clickable(interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(color = Color.Gray),
                        onClick = {

                        }),
                    text = "${NumberFormat.getInstance(Locale.US).format(product.price)} դր.",
                    color = textDark
                )
                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Gray)
                        .border(color = Color.Gray, width = 1.dp, shape = RoundedCornerShape(50)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .background(Color.White)
                            .clickable(interactionSource = MutableInteractionSource(),
                                indication = rememberRipple(color = rippleDark),
                                onClick = {
                                    if (count > 1) {
                                        count--
                                    }
                                })
                            .padding(10.dp), text = "-", color = Color.Gray
                    )
                    Text(
                        modifier = Modifier
                            .background(Color.White)
                            .border(color = Color.Gray, width = 1.dp, shape = RectangleShape)
                            .padding(10.dp),
                        text = "$count",
                        color = textDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier
                            .background(Color.White)
                            .clickable(interactionSource = MutableInteractionSource(),
                                indication = rememberRipple(color = rippleDark),
                                onClick = {
                                    count++
                                })
                            .padding(10.dp),
                        text = "+",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .clickable(interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(color = rippleDark),
                        onClick = {
                            onAddToCartClicked(count)
                        })
                    .padding(12.dp),
                painter = painterResource(id = R.drawable.ic_add_to_cart),
                contentDescription = null,
                tint = purple
            )
        }
    }

}

@Preview
@Composable
fun ItemProductPreview() {
    ItemProduct(
        product = Product(
            id = 0,
            name = "Iphone 13 Pro",
            price = 350000F,
            image = "https://www.mobilecentre.am/img/prodpic/small/114231.jpg"
        ), onAddToCartClicked = {}
    )
}
