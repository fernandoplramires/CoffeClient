package br.com.ramires.gourment.coffeclient.data.repository.device

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import br.com.ramires.gourment.coffeclient.data.model.Device
import kotlinx.coroutines.tasks.await

class FirebaseDeviceRepository : DeviceRepositoryInterface {

    private val db = FirebaseFirestore.getInstance()
    private val devicesCollection = db.collection("devices")

    init {
        db.collection("orders").get()
            .addOnSuccessListener {
                Log.d("FirebaseTest", "Connection successful, devices fetched.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "Connection failed on devices collection", e)
            }
    }

    override suspend fun isDeviceRegistered(deviceId: String): Boolean {
        return try {
            val querySnapshot = devicesCollection
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("status", true)
                .get()
                .await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching devices", e)
            false
        }
    }

    override suspend fun isUserValid(username: String, password: String): Boolean {
        return try {
            val userSnapshot = devicesCollection
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .whereEqualTo("status", true)
                .get()
                .await()

            !userSnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("FirebaseDeviceRepository", "Error to validade username/password", e)
            false
        }
    }

    override suspend fun getAllDevices(): List<Device> {
        return try {
            val snapshot = devicesCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Device::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error fetching devices", e)
            emptyList()
        }
    }

    override suspend fun addDevice(device: Device) {
        try {
            val docRef = devicesCollection.document() // Generate new document ID
            device.id = docRef.id.toInt() // Assuming the ID is stored as an Int
            docRef.set(device).await()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error add device", e)
        }
    }

    override suspend fun updateDevice(device: Device) {
        try {
            device.id?.let { id ->
                devicesCollection.document(id.toString()).set(device).await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error update device", e)
        }
    }

    override suspend fun deleteDevice(deviceId: Int) {
        try {
            devicesCollection.document(deviceId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("FirebaseOrderRepository", "Error delete device", e)
        }
    }
}

