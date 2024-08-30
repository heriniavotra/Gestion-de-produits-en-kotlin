package com.example.mggeittoky20

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import com.example.mggeittoky20.ui.theme.Mggeittoky20Theme
import java.io.FileOutputStream
import java.io.InputStream

class EditProduct : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val product = intent.getSerializableExtra("product") as? Product

        setContent {
            Mggeittoky20Theme {
                Scaffold {
                    EditProductScreen(
                        product = product,
                        onProductUpdated = { updatedProduct ->
                            val intent = Intent()
                            intent.putExtra("updatedProduct", updatedProduct)
                            setResult(RESULT_OK, intent)
                            finish()
                        },
                        goToMainActivity = {
                            finish()
                        }
                    )
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
}


