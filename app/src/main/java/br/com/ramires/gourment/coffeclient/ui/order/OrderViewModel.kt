package br.com.ramires.gourment.coffeclient.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepositoryInterface, private val deviceId: String) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            try {
                //val orderList = repository.getAllOrders()
                val orderList = repository.getAllOrdersByDeviceId(deviceId)
                //Log.d("FirebaseTest", "Load orders: ${orderList.toString()}")
                _orders.postValue(orderList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateOrder(updatedOrder: Order) {
        viewModelScope.launch {
            try {
                //Log.d("FirebaseTest", "Updating order: ${updatedOrder.toString()}")
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