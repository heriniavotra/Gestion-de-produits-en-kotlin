package com.example.mggeittoky20

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mggeittoky20.ui.theme.Mggeittoky20Theme
import java.io.FileOutputStream
import java.io.InputStream

class AddProduct : ComponentActivity() {
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mggeittoky20Theme {
                AddProductScreen(
                    onProductAdded = { product ->
                        val intent = Intent()
                        intent.putExtra("product", product)
                        setResult(RESULT_OK, intent)
                        finish()
                    },
                    goToMainActivity = {
                        navigateToMainActivity()
                    },
                    onImageSelected = {
                        selectedImageUri = it
                    }
                )
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@AddProduct, MainActivity::class.java)
        startActivity(intent)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onProductAdded: (Product) -> Unit,
    goToMainActivity: () -> Unit,
    onImageSelected: (Uri) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productImagePath by remember { mutableStateOf("") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                onImageSelected(it)
                val imagePath = saveImageToInternalStorage(context, it)
                productImagePath = imagePath ?: ""
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
                title = { Text("Ajout de produit") },
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
                .padding(16.dp)
                .padding(top = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Nom du produit") }
            )

            OutlinedTextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Prix du produit") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { /* Handle Next action if needed */ })
            )

            OutlinedTextField(
                value = productQuantity,
                onValueChange = { productQuantity = it },
                label = { Text("Quantité du produit") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { /* Handle Next action if needed */ })
            )

            OutlinedTextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Description du produit") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* Handle Done action if needed */ })
            )

            Button(
                onClick = {
                    launcher.launch("image/*")
                }
            ) {
                Text("Choisir une image")
            }

            Button(
                onClick = {
                    val product = Product(
                        name = productName,
                        price = productPrice.toDoubleOrNull() ?: 0.0,
                        quantity = productQuantity.toIntOrNull() ?: 0,
                        description = productDescription,
                        imagePath = productImagePath
                    )
                    onProductAdded(product)
                }
            ) {
                Text("Ajouter")
            }
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    val contentResolver = context.contentResolver
    val filePath = context.filesDir.path + "/" + System.currentTimeMillis() + ".jpg"
    try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(filePath)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return filePath
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

@Preview(showBackground = true)
@Composable
fun AddProductPreview() {
    Mggeittoky20Theme {
        AddProductScreen(
            onProductAdded = {},
            goToMainActivity = {},
            onImageSelected = {}
        )
    }
}
