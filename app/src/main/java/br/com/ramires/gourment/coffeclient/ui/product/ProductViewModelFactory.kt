package br.com.ramires.gourment.coffeclient.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.ramires.gourment.coffeclient.data.repository.product.ProductRepositoryInterface

class ProductViewModelFactory(private val repository: ProductRepositoryInterface) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
