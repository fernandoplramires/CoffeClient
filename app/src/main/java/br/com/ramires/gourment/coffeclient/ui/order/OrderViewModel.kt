package br.com.ramires.gourment.coffeclient.ui.order

import android.util.Log
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

    private fun loadOrders() {
        viewModelScope.launch {
            try {
                val orderList = repository.getAllOrders().toMutableList()
                Log.d("OrderViewModel", "repository.getAllOrders().toMutableList(): $orderList")

                // Verifica se há um pedido com status "CARRINHO"
                val cartOrder = orderList.find { it.status == OrderStatus.CARRINHO.toString() }
                if (cartOrder == null) {

                    // Adiciona o novo pedido no topo
                    val newCartOrder = repository.createOrder()
                    orderList.add(0, newCartOrder)
                    Log.d("OrderViewModel", "repository.createOrder(): $newCartOrder")
                }
                Log.d("OrderViewModel", "orderList.find { it.status == OrderStatus.CARRINHO.toString() }: $cartOrder")

                // Ordena e atualiza a lista
                val sortedOrders = orderList.sortedByDescending { it.status == OrderStatus.CARRINHO.toString() }
                _orders.postValue(sortedOrders)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
