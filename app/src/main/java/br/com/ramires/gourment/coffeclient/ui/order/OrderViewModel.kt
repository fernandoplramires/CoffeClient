package br.com.ramires.gourment.coffeclient.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.util.Validates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(private val repository: OrderRepositoryInterface) : ViewModel() {

    //Pedidos
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    /*TODO
    //Callback de Pagamento
    private val _eventNavigateToPayment = MutableLiveData<Order>()
    val eventNavigateToPayment: LiveData<Order> get() = _eventNavigateToPayment
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    */

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

    /*TODO
    fun finalizeOrder(order: Order) {
        viewModelScope.launch {
            try {
                // Atualiza o status do pedido para "NOVO"
                order.status = OrderStatus.NOVO.toString()
                repository.updateOrder(order)

                // Enviar requisição ao navegador
                _eventNavigateToPayment.postValue(order)
            } catch (e: Exception) {
                _errorMessage.postValue("Erro ao finalizar pedido: ${e.message}")
            }
        }
    }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    */
}
