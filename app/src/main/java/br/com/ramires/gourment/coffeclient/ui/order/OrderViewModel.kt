package br.com.ramires.gourment.coffeclient.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepositoryInterface) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    init {
        loadOrders()
    }

    fun loadOrders(onOrdersUpdated: (() -> Unit)? = null): Int? {
        var newCartOrderId: Int? = null
        viewModelScope.launch {
            try {
                val orderList = repository.getAllOrders().toMutableList()

                val cartOrder = orderList.find { it.status == OrderStatus.CARRINHO.toString() }
                if (cartOrder == null) {
                    val newCartOrder = repository.createOrder()
                    newCartOrderId = newCartOrder.id
                    orderList.add(newCartOrder)
                }

                val sortedOrders = orderList.sortedWith(
                    compareByDescending<Order> { it.status == OrderStatus.CARRINHO.toString() }
                        .thenByDescending { it.id }
                )

                _orders.postValue(sortedOrders)
                // Notifica que os pedidos foram atualizados
                onOrdersUpdated?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return newCartOrderId
    }

    fun getOrderById(orderId: Int): Order? {
        return orders.value?.find { it.id == orderId }
    }

    fun updateOrder(updatedOrder: Order) {
        viewModelScope.launch {
            try {
                updatedOrder.totalPrice = updatedOrder.calculateTotalPrice()
                repository.updateOrder(updatedOrder)
                loadOrders()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun expandOrder(orderId: Int) {
        _orders.value = _orders.value?.map { it.copy() }
    }
}
