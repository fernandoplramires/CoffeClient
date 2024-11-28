package br.com.ramires.gourment.coffeclient.data.repository.device

import br.com.ramires.gourment.coffeclient.data.model.Device

interface DeviceRepositoryInterface {
    suspend fun isDeviceRegistered(deviceId: String): Boolean
    suspend fun isUserValid(username: String, password: String): Boolean
    suspend fun getAllDevices(): List<Device>
    suspend fun addDevice(device: Device)
    suspend fun updateDevice(device: Device)
    suspend fun deleteDevice(deviceId: Int)
}
