package br.com.ramires.gourment.coffeclient.ui.product

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ramires.gourment.coffeclient.data.model.Product
import br.com.ramires.gourment.coffeclient.data.repository.product.ProductRepositoryInterface
import br.com.ramires.gourment.coffeclient.util.ImageUtils
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepositoryInterface) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    var editingProduct: Product? = null

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val orderList = repository.getAllProducts()
                _products.postValue(orderList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addProduct(context: Context, product: Product) {
        viewModelScope.launch {
            try {
                if (product.imageUrl != null) {
                    val imageUri = product.imageUrl.toUri()
                    val base64Image = ImageUtils.convertImageToBase64(context, imageUri)
                    val updatedProduct = product.copy(imageUrl = base64Image)
                    repository.addProduct(updatedProduct)
                } else {
                    repository.addProduct(product)
                }
                _products.value = repository.getAllProducts().toList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeProduct(productId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                _products.value = repository.getAllProducts().toList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startEditingProduct(product: Product) {
        editingProduct = product
    }

    fun saveEditedProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
                _products.value = repository.getAllProducts().toList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProductImage(context: Context, product: Product, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val base64Image = ImageUtils.convertImageToBase64(context, imageUri)
                val updatedProduct = product.copy(imageUrl = base64Image)
                repository.updateProduct(updatedProduct)
                _products.value = repository.getAllProducts().toList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
