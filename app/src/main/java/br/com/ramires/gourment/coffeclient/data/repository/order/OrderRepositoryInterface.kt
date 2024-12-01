package br.com.ramires.gourment.coffeclient.data.repository.order

import br.com.ramires.gourment.coffeclient.data.model.Order

interface OrderRepositoryInterface {
    suspend fun getAllOrders(): List<Order>
    suspend fun getOrderByStatus(status: String): Order?
    suspend fun createOrder(): Order
    suspend fun getNextProductId(): Int
    suspend fun addOrder(order: Order)
    suspend fun updateOrder(order: Order)
    suspend fun deleteOrder(orderId: Int)
}
