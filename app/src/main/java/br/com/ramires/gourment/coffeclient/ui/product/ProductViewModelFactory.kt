package br.com.ramires.gourment.coffeclient.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.data.repository.product.ProductRepositoryInterface

class ProductViewModelFactory(
    private val productsRepository: ProductRepositoryInterface,
    private val ordersRepository: OrderRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(productsRepository, ordersRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
