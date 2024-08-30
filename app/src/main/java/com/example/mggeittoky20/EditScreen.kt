package com.example.mggeittoky20

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mggeittoky20.ui.theme.Mggeittoky20Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    product: Product?,
    onProductUpdated: (Product) -> Unit,
    goToMainActivity: () -> Unit
) {
    product?.let {
        var productName by rememberSaveable { mutableStateOf(it.name) }
        var productPrice by rememberSaveable { mutableStateOf(it.price.toString()) }
        var productQuantity by rememberSaveable { mutableStateOf(it.quantity.toString()) }
        var productDescription by rememberSaveable { mutableStateOf(it.description) }
        var productImageUri by rememberSaveable { mutableStateOf(it.imagePath ?: "") }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                uri?.let {
                    coroutineScope.launch(Dispatchers.IO) {
                        val imagePath = (context as EditProduct).saveImageToInternalStorage(context, it)
                        if (imagePath != null) {
                            productImageUri = imagePath
                        }
                    }
                }
            }
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Red,
                        titleContentColor = Color.White
                    ),
                    title = { Text("Modification du produit") },
                    navigationIcon = {
                        IconButton(onClick = goToMainActivity) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(top = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { BasicText("Nom du produit") }
                )
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    label = { BasicText("Prix du produit") }
                )
                OutlinedTextField(
                    value = productQuantity,
                    onValueChange = { productQuantity = it },
                    label = { BasicText("Quantité du produit") }
                )
                OutlinedTextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    label = { BasicText("Description du produit") }
                )

                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Choisir une image")
                }

                Button(
                    onClick = {
                        val updatedProduct = Product(
                            name = productName,
                            price = productPrice.toDoubleOrNull() ?: 0.0,
                            quantity = productQuantity.toIntOrNull() ?: 0,
                            description = productDescription,
                            imagePath = productImageUri.takeIf { it.isNotEmpty() }
                        )
                        onProductUpdated(updatedProduct)
                    }
                ) {
                    Text("Enregistrer les modifications")
                }
                productImageUri.takeIf { it.isNotEmpty() }?.let { imageUri ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,

                        )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProductPreview() {
    val product = Product("Nom du produit", 10.0, 5, "Description du produit", "R.drawable.yaourt")
    Mggeittoky20Theme {
        EditProductScreen(
            product = product,
            onProductUpdated = {},
            goToMainActivity = {}
        )
    }
}
