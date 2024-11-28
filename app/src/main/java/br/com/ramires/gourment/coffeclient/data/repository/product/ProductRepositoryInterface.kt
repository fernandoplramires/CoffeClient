package br.com.ramires.gourment.coffeclient.data.repository.product

import br.com.ramires.gourment.coffeclient.data.model.Product

interface ProductRepositoryInterface {
    suspend fun getAllProducts(): List<Product>
    suspend fun getNextProductId(): Int
    suspend fun addProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(productId: Int)
}
