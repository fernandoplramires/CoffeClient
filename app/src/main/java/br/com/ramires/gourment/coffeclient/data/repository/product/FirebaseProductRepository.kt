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

    override suspend fun getNextProductId(): Int {
        var nextProductionId = 1
        try {
            // Busca o maior ID existente na coleção
            val maxId = productsCollection
                .orderBy("id", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.getLong("id") ?: 0L

            // Define o próximo ID sequencial
            nextProductionId = (maxId + 1).toInt()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error get next production id product", e)
        }
        return nextProductionId
    }

    override suspend fun addProduct(product: Product) {
        try {
            product.id = getNextProductId()
            productsCollection.document(product.id.toString()).set(product).await()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error add product", e)
        }
    }

    override suspend fun updateProduct(product: Product) {
        try {
            product.id?.let { id ->
                productsCollection.document(id.toString()).set(product).await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error update product", e)
        }
    }

    override suspend fun deleteProduct(productId: Int) {
        try {
            productsCollection.document(productId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error delete product", e)
        }
    }
}
