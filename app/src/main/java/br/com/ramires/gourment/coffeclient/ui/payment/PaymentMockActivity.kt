package br.com.ramires.gourment.coffeclient.ui.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.ramires.gourment.coffeclient.R

class PaymentMockActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_mock)

        webView = findViewById(R.id.webViewPayment)
        webView.settings.javaScriptEnabled = true // Ativa JavaScript

        // Carrega o arquivo HTML local
        webView.loadUrl("file:///android_asset/mock_payment.html")

        // Intercepta os cliques nos botões
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    handleCallback(it)
                }
                return true
            }
        }
    }

    private fun handleCallback(url: String) {
        val uri = Uri.parse(url)
        val status = uri.getQueryParameter("status")
        val orderId = intent.getIntExtra("ORDER_ID", -1)

        val resultIntent = Intent().apply {
            putExtra("PAYMENT_STATUS", status)
            putExtra("ORDER_ID", orderId)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener { finish() } // Finaliza a atividade ao fechar o alerta
            .show()
    }
}
