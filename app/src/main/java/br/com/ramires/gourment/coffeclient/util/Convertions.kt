package br.com.ramires.gourment.coffeclient.util

import java.text.NumberFormat
import java.util.Locale

object Convertions {

    fun formatToBrazilianCurrency(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return format.format(value)
    }
}
