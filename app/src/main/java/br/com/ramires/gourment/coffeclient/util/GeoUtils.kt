package br.com.ramires.gourment.coffeclient.util

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Modelos para a resposta da API do Google Maps
data class DistanceMatrixResponse(
    val rows: List<Row>
)

data class Row(
    val elements: List<Element>
)

data class Element(
    val distance: Distance?,
    val duration: Duration?,
    val status: String
)

data class Distance(
    val text: String,
    val value: Int // Distância em metros
)

data class Duration(
    val text: String,
    val value: Int // Duração em segundos
)

object GeoUtils {

    private const val GOOGLE_API_BASE_URL = "https://maps.googleapis.com/maps/api/"
    private const val GOOGLE_API_KEY = "AIzaSyB6P55zZ64x1qotjIAUToYVvn0UFcUjCuU"

    // Coordenadas do estabelecimento - CEP: 04001-002 (São Paulo, SP - Avenida Paulista)
    private const val ESTABLISHMENT_LATITUDE = -23.5614
    private const val ESTABLISHMENT_LONGITUDE = -46.6554

    private var MOCK_MODE = true

    fun setMockMode(repositoryType: String) {
        MOCK_MODE = repositoryType == "MOCK"
        Log.d("GeoUtils", "MOCK_MODE set to $MOCK_MODE based on repositoryType: $repositoryType")
    }

    fun isMockMode(): Boolean = MOCK_MODE

    // Interface da API do Google Maps
    interface GoogleMapsApi {
        @GET("distancematrix/json")
        suspend fun getDistance(
            @Query("origins") origins: String,
            @Query("destinations") destinations: String,
            @Query("key") apiKey: String
        ): DistanceMatrixResponse
    }

    // Retrofit Instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(GOOGLE_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val googleApiService: GoogleMapsApi = retrofit.create(GoogleMapsApi::class.java)

    // Cálculo de distância com o Haversine
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadiusKm * c
    }

    // Valida se o CEP está dentro do raio usando coordenadas
    fun isWithinRadius(lat: Double, lon: Double, radiusKm: Double): Boolean {
        val distance = calculateDistance(ESTABLISHMENT_LATITUDE, ESTABLISHMENT_LONGITUDE, lat, lon)
        return distance <= radiusKm
    }

    // Validação com mock (baseado no final do CEP)
    private fun isWithinRadiusMock(zipCode: String): Boolean {
        return when (zipCode.takeLast(1)) {
            "1" -> true  // Dentro do raio
            "0" -> false // Fora do raio
            else -> zipCode.length % 2 == 0 // Exemplo: outros casos são aleatórios
        }
    }

    suspend fun isWithinRadiusUsingApi(zipCode: String): Boolean {
        Log.d("GeoUtils", "MOCK_MODE: $MOCK_MODE, Processing zipCode: $zipCode")
        return if (MOCK_MODE) {
            isWithinRadiusMock(zipCode).also {
                Log.d("GeoUtils", "Mock result for $zipCode: $it")
            }
        } else {
            try {
                val origins = "$ESTABLISHMENT_LATITUDE,$ESTABLISHMENT_LONGITUDE"
                val response = googleApiService.getDistance(origins, zipCode, GOOGLE_API_KEY)
                val distanceMeters = response.rows.firstOrNull()?.elements?.firstOrNull()?.distance?.value
                val result = distanceMeters != null && distanceMeters / 1000 <= 60
                Log.d("GeoUtils", "API result for $zipCode: $result (distance: $distanceMeters meters)")
                result
            } catch (e: Exception) {
                Log.e("GeoUtils", "Error fetching distance for $zipCode", e)
                false
            }
        }
    }

    // Método para teste
    suspend fun testGoogleMapsApi(zipCode: String) {
        val isValid = isWithinRadiusUsingApi(zipCode)
        Log.d("GeoUtils", "CEP $zipCode está dentro do raio de 60km? $isValid")
    }
}
