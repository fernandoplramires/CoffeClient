package br.com.ramires.gourment.coffeclient.data.model

import com.google.firebase.firestore.PropertyName

data class Product(
    @PropertyName("id") var id: Int? = null,
    @PropertyName("name") val name: String? = null,
    @PropertyName("description") val description: String? = null,
    @PropertyName("price") val price: Double? = null,
    @PropertyName("imageUrl") val imageUrl: String? = null
){
    // Construtor vazio para o Firestore
    constructor() : this(null, null, null, null, null)
}
