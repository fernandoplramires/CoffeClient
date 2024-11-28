package br.com.ramires.gourment.coffeclient.data.repository.order

import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderDetail
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus

class MockOrderRepository : OrderRepositoryInterface {

    private var currentMaxId = 0
    private val orders = mutableListOf<Order>(
        Order(
            391,
            "946fcb4057657c1a",
            listOf(
                OrderDetail(
                    "Cookie Cake Red Velvet Oreo 300g",
                    1
                ),
                OrderDetail(
                    "Cookie Cake Nutella 300g",
                    2
                ),
                OrderDetail(
                    "Cupcake Buenasso 300g",
                    2
                )
            ),
            145.0,
            "joselito@uol.com",
            "(11) 98877-6655",
            "06010-100",
            "Apto 9",
            "99",
            OrderStatus.NOVO.toString()
        ),
        Order(
            2,
            "0000000000000",
            listOf(
                OrderDetail(
                "Cookie Cake Red Velvet Oreo 300g",
                2
                )
            ),
            75.0,
            "hermes@uol.com",
            "(11) 98877-1255",
            "06010-101",
            "Casa",
            "20",
            OrderStatus.PREPARACAO.toString()
        ),
        Order(
            3,
            "0000000000000",
            listOf(
                OrderDetail(
                    "Cookie Cake Red Velvet Oreo 300g",
                    1
                )
            ),
            25.0,
            "hermes@uol.com",
            "(11) 98877-1255",
            "06010-101",
            "Casa",
            "20",
            OrderStatus.CARRINHO.toString()
        )
    )

    override suspend fun getAllOrders(): List<Order> {
        return orders
    }

    override suspend fun getAllOrdersForManagement(): List<Order> {
        return orders.filter { it.status != "CARRINHO" }
    }

    override suspend fun getAllOrdersByDeviceId(deviceId: String): List<Order> {
        return orders.filter { it.deviceId == deviceId }
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
