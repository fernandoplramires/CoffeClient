package br.com.ramires.gourment.coffeclient.data.model

import com.google.firebase.firestore.PropertyName

enum class OrderStatus(val displayName: String) {
    CARRINHO("Carrinho"),
    NOVO("Novo"),
    CANCELADO("Cancelado"),
    PREPARACAO("Em preparação"),
    FINALIZADO("Finalizado")
}

data class OrderDetail(
    @PropertyName("productName") var productName: String? = null,
    @PropertyName("productPrice") var productPrice: Double? = 0.00,
    @PropertyName("quantity") var quantity: Int? = null
) {
    constructor() : this(null, null, null)
}

data class Order(
    @PropertyName("id") var id: Int? = null,
    @PropertyName("deviceId") var deviceId: String? = null,
    @PropertyName("details") var details: List<OrderDetail>? = null,
    @PropertyName("totalPrice") var totalPrice: Double? = null,
    @PropertyName("email") var email: String? = null,
    @PropertyName("phone") var phone: String? = null,
    @PropertyName("zipCode") var zipCode: String? = null,
    @PropertyName("complement") var complement: String? = null,
    @PropertyName("number") var number: String? = null,
    @PropertyName("status") var status: String? = null
) {
    // Construtor vazio para o Firestore
    constructor() : this(null, null, null, null, null, null, null, null, null, null)

    // Método para calcular o total baseado nos detalhes
    fun calculateTotalPrice(): Double {
        return details?.sumOf { (it.quantity ?: 0) * (it.productPrice ?: 0.0) } ?: 0.0
    }
}
