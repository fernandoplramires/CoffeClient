package br.com.ramires.gourment.coffeclient.util

import android.util.Patterns

object Validates {

    fun isEmail(email: String?): Boolean {
        // Valida formato de e-mail
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isCep(cep: String?): Boolean {
        // Valida formato de CEP
        return cep != null && cep.matches(Regex("\\d{5}-\\d{3}"))
    }

    fun isPhone(phone: String?): Boolean {
        // Valida formato de telefone, (XX) XXXX-XXXX ou (XX) XXXXX-XXXX
        val regex = Regex("^\\(\\d{2}\\) \\d{4,5}-\\d{4}$")
        return phone != null && regex.matches(phone)
    }
}
