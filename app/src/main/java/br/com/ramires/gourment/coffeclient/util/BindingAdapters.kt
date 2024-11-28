package br.com.ramires.gourment.coffeclient.util

import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("orderDetails")
    fun setOrderDetails(textView: TextView, details: List<String>?) {
        textView.text = details?.joinToString(separator = "\n") ?: ""
    }
}
