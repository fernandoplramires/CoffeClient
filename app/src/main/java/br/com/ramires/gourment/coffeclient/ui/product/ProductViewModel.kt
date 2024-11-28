package br.com.ramires.gourment.coffeclient.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderDetail
import br.com.ramires.gourment.coffeclient.data.model.Product
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.data.repository.product.ProductRepositoryInterface
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productsRepository: ProductRepositoryInterface,
    private val ordersRepository: OrderRepositoryInterface
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _cartOrder = MutableLiveData<Order?>()
    val cartOrder: LiveData<Order?> get() = _cartOrder

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val orderList = productsRepository.getAllProducts()
                _products.postValue(orderList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadCartOrder() {
        viewModelScope.launch {
            try {
                val order = ordersRepository.getOrderByStatus("CARRINHO")
                _cartOrder.postValue(order)
            } catch (e: Exception) {
                e.printStackTrace()
                _cartOrder.postValue(null)
            }
        }
    }

    fun addProductToCart(product: Product) {
        viewModelScope.launch {
            try {
                val order = ordersRepository.getOrderByStatus("CARRINHO") ?: ordersRepository.createOrder("CARRINHO")
                val updatedDetails = order.details.orEmpty().toMutableList()

                // Adiciona o produto ao carrinho com quantidade 1
                updatedDetails.add(OrderDetail(product.id, product.name, product.price, 1))
                order.details = updatedDetails
                ordersRepository.updateOrder(order)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeProductFromCart(product: Product) {
        viewModelScope.launch {
            try {
                val order = ordersRepository.getOrderByStatus("CARRINHO") ?: return@launch
                val updatedDetails = order.details.orEmpty().toMutableList()

                // Remove o produto do carrinho
                updatedDetails.removeIf { it.id == product.id }
                order.details = updatedDetails
                ordersRepository.updateOrder(order)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
