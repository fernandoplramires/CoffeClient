package br.com.ramires.gourment.coffeclient.data.model

import com.google.firebase.firestore.PropertyName

data class Device(
    @PropertyName("id") var id: Int? = null,
    @PropertyName("deviceId") val deviceId: String? = null,
    @PropertyName("username") val username: String? = null,
    @PropertyName("password") val password: String? = null,
    @PropertyName("status") val status: Boolean? = null
){
    // Construtor vazio para o Firestore
    constructor() : this(null, null, null)
}