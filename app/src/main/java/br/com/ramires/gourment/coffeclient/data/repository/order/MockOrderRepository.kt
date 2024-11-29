package br.com.ramires.gourment.coffeclient.data.repository.order

import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderDetail
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus

class MockOrderRepository(private val deviceId: String) : OrderRepositoryInterface {

    private var currentMaxId = 0
    private val orders = mutableListOf<Order>(
        Order(
            391,
            "946fcb4057657c1aXXX",
            listOf(
                OrderDetail(
                    1,
                    "Cookie Cake Red Velvet Oreo 300g",
                    35.5,
                    1
                ),
                OrderDetail(
                    2,
                    "Cupcake Buenasso 300g",
                    20.0,
                    2
                )
            ),
            126.5,
            "joselito@uol.com",
            "(11) 98877-6655",
            "06010-100",
            "Apto 9",
            "99",
            OrderStatus.CARRINHO.toString()
        ),
        Order(
            2,
            "946fcb4057657c1a",
            listOf(
                OrderDetail(
                1,
                "Cookie Cake Red Velvet Oreo 300g",
                35.5,
                2
                )
            ),
            61.0,
            "joselito@uol.com",
            "(11) 98877-6655",
            "06010-100",
            "Apto 9",
            "99",
            OrderStatus.PREPARACAO.toString()
        )
    )

    override suspend fun getAllOrders(): List<Order> {
        return orders.filter { it.deviceId == deviceId }
    }

    override suspend fun getOrderByStatus(status: String): Order? {
        return orders.find { it.deviceId == deviceId && it.status == status }
    }

    override suspend fun createOrder(): Order {
        val newOrder = Order(
            id = getNextProductId(),
            deviceId = deviceId,
            details = mutableListOf(),
            status = OrderStatus.CARRINHO.toString()
        )
        orders.add(newOrder)
        return newOrder
    }

    override suspend fun getNextProductId(): Int {
        return ++currentMaxId
    }

    override suspend fun addOrder(order: Order) {
        orders.add(order)
    }

    override suspend fun updateOrder(order: Order) {
        val index = orders.indexOfFirst { it.id == order.id }
        if (index != -1) {
            orders[index] = order
        } else {
            throw IllegalArgumentException("Pedido com id ${order.id} não encontrado.")
        }
    }

    override suspend fun deleteOrder(orderId: Int) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            orders.removeAt(index)
        } else {
            throw IllegalArgumentException("Pedido com id ${orderId} não encontrado.")
        }
    }
}
