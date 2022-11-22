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
import com.roberthayrapetyan.shop.ui.theme.*
import com.roberthayrapetyan.shop.ui.viewmodels.CartScreenViewModel
import java.util.*

@Preview
@Composable
fun CartScreen(viewModel: CartScreenViewModel? = null, navController: NavHostController? = null) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val profile = viewModel?.profile?.observeAsState()
    val cartTotalPrice = viewModel?.cartTotalPrice?.observeAsState()

    val toastSuccessOrder = stringResource(id = R.string.label_order_placed)

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
    val orderEnabled = viewModel?.orderEnabled?.observeAsState()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CartToolbar(profile?.value, cartTotalPrice = cartTotalPrice?.value ?: 0f, onBackPressed = {
            navController?.popBackStack()
        })
    }, backgroundColor = background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                items(viewModel?.cartItems ?: emptyList()) { product ->
                    ItemCartProduct(product = product, onRemoveClicked = {
                        viewModel?.removeFromCart(product)
                    })
                }
            }
            if (viewModel?.cartItems?.isNotEmpty() == true) {
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (orderEnabled?.value == true) purple else Color.Gray.copy(alpha = 0.8f)
                        )
                        .align(Alignment.BottomCenter)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = if (orderEnabled?.value == true) rememberRipple(color = rippleLight) else null,
                            onClick = {
                                if (orderEnabled?.value == true) {
                                    viewModel.checkout()
                                    Toast
                                        .makeText(context, toastSuccessOrder, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        )
                        .padding(vertical = 10.dp, horizontal = 24.dp),
                    text = stringResource(id = R.string.label_chackout) + " ${
                        NumberFormat.getInstance(Locale.US).format(viewModel.orderSum ?: 0f)
                    } դր.",
                    color = textLight,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            if (viewModel?.cartItems?.isEmpty() == true) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(id = R.string.label_cart_is_empty),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

        }
    }
}

@Composable
fun ItemCartProduct(
    modifier: Modifier = Modifier,
    product: Product,
    onRemoveClicked: () -> Unit
) {
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
                Text(
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    text = stringResource(id = R.string.label_count, product.count),
                    color = textLight
                )
            }
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .clickable(interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(color = rippleDark),
                        onClick = {
                            onRemoveClicked()
                        })
                    .padding(12.dp),
                painter = painterResource(id = R.drawable.ic_remove),
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

@Preview
@Composable
fun ItemCartProductPreview() {
    ItemCartProduct(
        product = Product(
            id = 0,
            name = "Iphone 13 Pro",
            price = 350000F,
            image = "https://www.mobilecentre.am/img/prodpic/small/114231.jpg",
            count = 5
        ),
        onRemoveClicked = {}
    )
}

@Composable
fun CartToolbar(profile: Profile?, cartTotalPrice: Float, onBackPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(topBarBackground)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = rippleLight),
                    onClick = {
                        onBackPressed()
                    }
                )
                .padding(12.dp),
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = null,
            tint = textLight
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            text = stringResource(id = R.string.title_cart),
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
            modifier = Modifier.padding(start = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = profile?.name ?: "Name",
                color = textLight,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${
                    NumberFormat.getInstance(Locale.US)
                        .format(profile?.balance?.minus(cartTotalPrice) ?: 0f)
                } դր.", color = textLight, fontWeight = FontWeight.Bold, fontSize = 14.sp
            )
        }
    }
}
