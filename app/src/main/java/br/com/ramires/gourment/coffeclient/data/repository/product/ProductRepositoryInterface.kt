package br.com.ramires.gourment.coffeclient.data.repository.product

import br.com.ramires.gourment.coffeclient.data.model.Product

interface ProductRepositoryInterface {
    suspend fun getAllProducts(): List<Product>
}
