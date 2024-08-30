package com.example.mggeittoky20
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ProductManager(private val context: Context) {
    private val gson = Gson()

    fun saveProducts(products: List<Product>) {
        val file = File(context.filesDir, "products.json")
        file.writeText(gson.toJson(products))
    }

    fun loadProducts(): List<Product> {
        val file = File(context.filesDir, "products.json")
        if (file.exists()) {
            val serializedProducts = file.readText()
            val type = object : TypeToken<List<Product>>() {}.type
            return gson.fromJson(serializedProducts, type)
        }
        return emptyList()
    }
}
