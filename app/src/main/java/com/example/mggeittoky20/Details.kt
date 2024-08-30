package com.example.mggeittoky20

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mggeittoky20.ui.theme.Mggeittoky20Theme

class Details : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val product = intent.getSerializableExtra("product") as Product

        setContent {
            Mggeittoky20Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ProductDetailScreen(product, goToMainActivity = { navigateToMainActivity() })
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@Details, MainActivity::class.java)
        startActivity(intent)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: Product, goToMainActivity: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    Color.Red,
                    titleContentColor = Color.White
                ),
                title = { Text("Détail du produit") },
                navigationIcon = {
                    IconButton(onClick = { goToMainActivity() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 100.dp),
            elevation = CardDefaults.elevatedCardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .border(2.dp, Color.Red)
                    .padding(bottom = 30.dp)
            ) {
                val context = LocalContext.current
                product.imagePath?.let { imageUri ->
                    val painter = rememberAsyncImagePainter(imageUri)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 350.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = product.name,
                    color = Color.Red,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Price: ${product.price} $",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Quantity: ${product.quantity}",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = product.description,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailsPreview() {
    val context = LocalContext.current
    val imageUri = Uri.parse("android.resource://${context.packageName}/${R.drawable.tournevis}")
    val imagePath = imageUri.toString()

    val product = Product(
        name = "Yaourt",
        price = 1.0,
        quantity = 23,
        description = "Produit laitier",
        imagePath = imagePath
    )

    Mggeittoky20Theme {
        ProductDetailScreen(product, goToMainActivity = {})
    }
}
