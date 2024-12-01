package br.com.ramires.gourment.coffeclient.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

object Masks {

    // Aplica a máscara de CEP
    fun applyCepMask(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "#####-###"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val unmasked = editable.toString().replace("-", "")
                val masked = buildMaskedCep(unmasked)
                editText.setText(masked)
                editText.setSelection(masked.length)

                isUpdating = false
            }

            private fun buildMaskedCep(unmasked: String): String {
                return when {
                    unmasked.length > 5 -> "${unmasked.substring(0, 5)}-${unmasked.substring(5)}"
                    else -> unmasked
                }
            }
        })
    }

    fun applyPhoneMask(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val unmasked = editable.toString().replace("[^\\d]".toRegex(), "")
                val masked = buildMaskedPhone(unmasked)
                editText.setText(masked)
                editText.setSelection(masked.length)

                isUpdating = false
            }

            private fun buildMaskedPhone(unmasked: String): String {
                return when {
                    unmasked.length > 10 -> "(${unmasked.substring(0, 2)}) ${unmasked.substring(2, 7)}-${unmasked.substring(7, 11)}"
                    unmasked.length > 6 -> "(${unmasked.substring(0, 2)}) ${unmasked.substring(2, 6)}-${unmasked.substring(6)}"
                    unmasked.length > 2 -> "(${unmasked.substring(0, 2)}) ${unmasked.substring(2)}"
                    unmasked.isNotEmpty() -> "(${unmasked.substring(0)}"
                    else -> unmasked
                }
            }
        })
    }
}
