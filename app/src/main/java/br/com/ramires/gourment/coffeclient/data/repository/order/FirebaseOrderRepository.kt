package br.com.ramires.gourment.coffeclient.data.repository.order

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirebaseOrderRepository(private val deviceId: String) : OrderRepositoryInterface {

    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    init {
        db.collection("orders").get()
            .addOnSuccessListener {
                Log.d("FirebaseTest", "Connection successful, orders fetched.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "Connection failed on orders collection", e)
            }
    }

    override suspend fun getAllOrders(): List<Order> {
        return try {
            val snapshot = ordersCollection.whereEqualTo("deviceId", deviceId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching orders", e)
            emptyList()
        }
    }

    override suspend fun getOrderByStatus(status: String): Order? {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("status", status)
                .get()
                .await()
            Log.d("FirebaseOrderRepository", "getOrderByStatus(): snapshot: $snapshot")

            val orders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
            Log.d("FirebaseOrderRepository", "getOrderByStatus(): orders $orders")

            if (orders.isNotEmpty()) {
                Log.d("FirebaseOrderRepository", "getOrderByStatus(): Found order $orders[0]")
            }

            Log.d("FirebaseOrderRepository", "getOrderByStatus(): orders.firstOrNull(): $orders.firstOrNull()")
            orders.firstOrNull()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching order by status: $status", e)
            null
        }
    }

    override suspend fun createOrder(): Order {

        val existingOrder = getOrderByStatus(OrderStatus.CARRINHO.toString())
        if (existingOrder != null) {
            Log.d("FirebaseOrderRepository", "createOrder(): Order already exists: $existingOrder")
            return existingOrder
        }
        Log.d("FirebaseOrderRepository", "createOrder(): Order not exists, creating...")

        val newOrder = Order(
            id = getNextProductId(),
            deviceId = deviceId,
            status = OrderStatus.CARRINHO.toString(),
            details = mutableListOf()
        )
        try {
            addOrder(newOrder)
            Log.d("FirebaseOrderRepository", "createOrder(): Created new order: $newOrder")
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error add order", e)
        }
        return newOrder
    }

    override suspend fun getNextProductId(): Int {
        return try {
            val snapshot = ordersCollection.get().await()
            val count = snapshot.size()
            count + 1
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching document count for next ID", e)
            generateUniqueRandomId() // Fallback para um ID aleatório único
        }
    }

    private suspend fun generateUniqueRandomId(): Int {
        return try {
            val existingIds = ordersCollection.get().await()
                .documents.mapNotNull { it.getLong("id")?.toInt() }

            // Garante que o ID gerado seja único
            var newId: Int
            do {
                // Gera um número aleatório positivo
                newId = Random.nextInt(1, Int.MAX_VALUE)
            } while (existingIds.contains(newId))

            newId
        } catch (e: Exception) {
            // Retorna um ID aleatório simples como último recurso
            Log.e("FirebaseOrderRepository", "Error generating unique random ID as fallback", e)
            Random.nextInt(1, Int.MAX_VALUE)
        }
    }

    override suspend fun addOrder(order: Order) {
        try {
            // Adiciona o pedido com o ID gerado
            val docRef = ordersCollection.document(order.id.toString())
            docRef.set(order).await()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error add order", e)
        }
    }

    override suspend fun updateOrder(order: Order) {
        try {
            order.id?.let { id ->
                ordersCollection.document(id.toString()).set(order).await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error update order", e)
        }
    }

    override suspend fun deleteOrder(orderId: Int) {
        try {
            ordersCollection.document(orderId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error delete order", e)
        }
    }
}
