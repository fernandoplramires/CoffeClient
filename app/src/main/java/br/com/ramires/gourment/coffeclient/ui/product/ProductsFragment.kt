package br.com.ramires.gourment.coffeclient.ui.product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.ramires.gourment.coffeclient.data.model.Product
import br.com.ramires.gourment.coffeclient.data.repository.product.ProductRepositoryInterface
import br.com.ramires.gourment.coffeclient.databinding.FragmentProductsBinding

class ProductsFragment(private val repository: ProductRepositoryInterface) : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    // Adicionado ActivityResultLauncher para substituir o startActivityForResult
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                val editingProduct = viewModel.editingProduct
                if (editingProduct != null && selectedImageUri != null) {
                    viewModel.updateProductImage(requireContext(), editingProduct, selectedImageUri)
                    adapter.notifyItemChanged(adapter.getPositionById(editingProduct.id!!))
                }
            }
        }

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

        val factory = ProductViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProductViewModel::class.java)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { action, product ->
            when (action) {
                ProductAdapter.ActionType.EDIT -> viewModel.startEditingProduct(product)
                ProductAdapter.ActionType.REMOVE -> showRemoveConfirmationDialog(product)
                ProductAdapter.ActionType.SAVE -> viewModel.saveEditedProduct(product)
                ProductAdapter.ActionType.UPLOAD_IMAGE -> selectImageFromGallery()
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun showRemoveConfirmationDialog(product: Product) {
        //TODO
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            if (products.isNullOrEmpty()) {
                binding.textViewEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.textViewEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(products)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
