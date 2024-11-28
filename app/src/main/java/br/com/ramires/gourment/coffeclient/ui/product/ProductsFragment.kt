package br.com.ramires.gourment.coffeclient.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.data.repository.product.ProductRepositoryInterface
import br.com.ramires.gourment.coffeclient.databinding.FragmentProductsBinding

class ProductsFragment(
    private val productsRepository: ProductRepositoryInterface,
    private val ordersRepository: OrderRepositoryInterface
) : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ProductViewModelFactory(productsRepository, ordersRepository)
        viewModel = ViewModelProvider(this, factory).get(ProductViewModel::class.java)

        setupRecyclerView()

        // Carregar o pedido com status "CARRINHO" primeiro
        viewModel.loadCartOrder()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { action, product ->
            when (action) {
                ProductAdapter.ActionType.SAVE -> viewModel.addProductToCart(product)
                ProductAdapter.ActionType.REMOVE -> viewModel.removeProductFromCart(product)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->

            // Lista vazia de produtos
            if (products.isNullOrEmpty()) {
                binding.textViewEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.textViewEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(products)
            }
        }

        // Observa o pedido com status "CARRINHO"
        viewModel.cartOrder.observe(viewLifecycleOwner) { cartOrder ->
            val selectedProductIds = cartOrder?.details?.mapNotNull { it.id } ?: emptyList()
            adapter.setSelectedProducts(selectedProductIds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
