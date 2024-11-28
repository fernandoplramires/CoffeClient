package br.com.ramires.gourment.coffeclient.data.repository.order

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import br.com.ramires.gourment.coffeclient.data.model.Order
import kotlinx.coroutines.tasks.await

class FirebaseOrderRepository : OrderRepositoryInterface {

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
            val snapshot = ordersCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching orders", e)
            emptyList()
        }
    }

    override suspend fun getAllOrdersForManagement(): List<Order> {
        return try {
            val snapshot = ordersCollection.whereNotEqualTo("status","CARRINHO").get().await()
            snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching orders", e)
            emptyList()
        }
    }

    override suspend fun getAllOrdersByDeviceId(deviceId: String): List<Order> {
        return try {
            val snapshot = ordersCollection.whereEqualTo("deviceId", deviceId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching orders", e)
            emptyList()
        }
    }

    override suspend fun getNextProductId(): Int {
        val metadataRef = db.collection("metadata").document("productsMaxId")
        return db.runTransaction { transaction ->
            val snapshot = transaction.get(metadataRef)
            val currentMaxId = snapshot.getLong("value")?.toInt() ?: 0
            transaction.update(metadataRef, "value", currentMaxId + 1)
            currentMaxId + 1
        }.await()
    }

    override suspend fun addOrder(order: Order) {
        try {
            val docRef = ordersCollection.document() // Generate new document ID
            order.id = docRef.id.toInt() // Assuming the ID is stored as an Int
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

