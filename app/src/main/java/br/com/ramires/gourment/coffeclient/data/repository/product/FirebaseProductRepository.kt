package br.com.ramires.gourment.coffeclient.data.repository.product

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import br.com.ramires.gourment.coffeclient.data.model.Product
import kotlinx.coroutines.tasks.await

class FirebaseProductRepository : ProductRepositoryInterface {

    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    init {
        db.collection("products").get()
            .addOnSuccessListener {
                Log.d("FirebaseTest", "Connection successful, products fetched.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "Connection failed on products collection", e)
            }
    }

    override suspend fun getAllProducts(): List<Product> {
        return try {
            val snapshot = productsCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching orders", e)
            emptyList()
        }
    }
}
