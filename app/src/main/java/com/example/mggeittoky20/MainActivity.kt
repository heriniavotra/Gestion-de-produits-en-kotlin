package com.example.mggeittoky20

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import com.example.mggeittoky20.ui.theme.Mggeittoky20Theme
import java.io.File
import java.io.InputStream
import java.io.Serializable

data class Product(
    val name: String,
    val price: Double,
    val quantity: Int,
    val description: String,
    val imagePath: String?
) : Serializable

class MainActivity : ComponentActivity() {
    private lateinit var productManager: ProductManager
    private val products = mutableStateListOf<Product>()
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productManager = ProductManager(this)
        loadProducts()

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let { uri ->
                    val imagePath = saveImageToInternalStorage(uri)
                }
            }
        }

        setContent {
            Mggeittoky20Theme {
                AllProduct(
                    products = products,
                    onClick = { product ->
                        navigateToDetails(product)
                    },
                    addProduct = {
                        navigateToAddProduct()
                    },
                    onEditClick = { product ->
                        navigateToEditProduct(product)
                    },
                    onDeleteClick = { product ->
                        deleteProduct(product)
                    }
                )
            }
        }
    }

    private fun loadProducts() {
        products.addAll(productManager.loadProducts())
    }

    private val editProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedProduct = result.data?.getSerializableExtra("updatedProduct") as? Product
            updatedProduct?.let { product ->
                val index = products.indexOfFirst { it.name == product.name }
                if (index != -1) {
                    products[index] = product
                    productManager.saveProducts(products)
                }
            }
        }
    }

    private fun navigateToEditProduct(product: Product) {
        val intent = Intent(this@MainActivity, EditProduct::class.java).apply {
            putExtra("product", product)
        }
        editProductLauncher.launch(intent)
    }

    private fun deleteProduct(product: Product) {
        products.remove(product)
        productManager.saveProducts(products)
    }

    private fun navigateToDetails(product: Product) {
        val intent = Intent(this@MainActivity, Details::class.java).apply {
            putExtra("product", product)
        }
        startActivity(intent)
    }

    private fun navigateToAddProduct() {
        val intent = Intent(this, AddProduct::class.java)
        addProductLauncher.launch(intent)
    }

    private val addProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val product = result.data?.getSerializableExtra("product") as? Product
            product?.let {
                products.add(it)
                productManager.saveProducts(products)
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream = file.outputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }
        return file.absolutePath
    }
}
