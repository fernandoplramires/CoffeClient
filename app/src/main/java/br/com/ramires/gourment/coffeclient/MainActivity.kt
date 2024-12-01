package br.com.ramires.gourment.coffeclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.ramires.gourment.coffeclient.data.repository.order.FirebaseOrderRepository
import br.com.ramires.gourment.coffeclient.data.repository.order.MockOrderRepository
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.data.repository.product.FirebaseProductRepository
import br.com.ramires.gourment.coffeclient.data.repository.product.MockProductRepository
import br.com.ramires.gourment.coffeclient.data.repository.product.ProductRepositoryInterface
import br.com.ramires.gourment.coffeclient.databinding.ActivityMainBinding
import br.com.ramires.gourment.coffeclient.ui.order.OrdersFragment
import br.com.ramires.gourment.coffeclient.ui.product.ProductsFragment
import br.com.ramires.gourment.coffeclient.util.GeoUtils
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var productRepository: ProductRepositoryInterface? = null
    private var orderRepository: OrderRepositoryInterface? = null

    companion object {
        fun start(
            context: Context,
            repositoryType: String
        ) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("REPOSITORY_TYPE", repositoryType)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val repositoryType = intent.getStringExtra("REPOSITORY_TYPE")
        initializeRepositories(repositoryType, deviceId)

        GeoUtils.setMockMode(repositoryType ?: "REAL")
        Log.d("MainActivity", "GeoUtils MOCK_MODE is set to: ${GeoUtils.isMockMode()}")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabLayoutStyle()
        setupTabListener()

        replaceFragment(ProductsFragment(productRepository!!, orderRepository!!))
    }

    private fun initializeRepositories(repositoryType: String?, deviceId: String) {
        if (repositoryType == "MOCK") {
            productRepository = MockProductRepository()
            orderRepository = MockOrderRepository(deviceId)
        } else {
            productRepository = FirebaseProductRepository()
            orderRepository = FirebaseOrderRepository(deviceId)
        }
    }

    private fun setupTabLayoutStyle() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Produtos"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pedidos"))

        binding.tabLayout.getTabAt(0)?.view?.setBackgroundResource(R.drawable.tab_selected_background)
        binding.tabLayout.getTabAt(1)?.view?.setBackgroundResource(R.drawable.tab_default_background)
    }

    private fun setupTabListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.tab_selected_background)
                when (tab?.position) {
                    0 -> replaceFragment(ProductsFragment(productRepository!!, orderRepository!!))
                    1 -> replaceFragment(OrdersFragment(orderRepository!!))
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.tab_default_background)
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Não faz nada no momento
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
