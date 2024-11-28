package br.com.ramires.gourment.coffeclient.ui.order

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepositoryInterface) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    init {
        loadOrders()
    }

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun loadOrders() {
        viewModelScope.launch {
            try {
                //val orderList = repository.getAllOrders()
                val orderList = repository.getAllOrdersForManagement()
                //Log.d("FirebaseTest", "Load orders: ${orderList.toString()}")
                _orders.postValue(orderList)
            } catch (e: Exception) {
                // Trate exceções, se necessário
                e.printStackTrace()
            }
        }
    }

    fun updateOrder(updatedOrder: Order) {
        viewModelScope.launch {
            try {
                //Log.d("FirebaseTest", "Updating order: ${updatedOrder.toString()}")
                repository.updateOrder(updatedOrder)
                loadOrders()
            } catch (e: Exception) {
                // Trate exceções, se necessário
                e.printStackTrace()
            }
        }
    }

    fun expandOrder(orderId: Int) {
        _orders.value = _orders.value?.map { it.copy() }
    }
}