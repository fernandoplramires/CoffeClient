package br.com.ramires.gourment.coffeclient.ui.splash

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.ramires.gourment.coffeclient.MainActivity
import br.com.ramires.gourment.coffeclient.databinding.ActivitySplashBinding
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var repositoryType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        promptUserForRepository()
    }

    private fun promptUserForRepository() {
        val options = arrayOf("Mock Repository", "Real Repository")
        AlertDialog.Builder(this)
            .setTitle("Escolha o Tipo de Repositório")
            .setSingleChoiceItems(options, -1) { dialog, which ->
                if (which == 0) {
                    repositoryType = "MOCK"
                } else {
                    repositoryType = "REAL"
                }

                dialog.dismiss()
                startMainActivity()
            }
            .setCancelable(false)
            .show()
    }

    private fun startMainActivity() {
        lifecycleScope.launch {
                MainActivity.start(this@SplashActivity, repositoryType)
        }
    }
}
