package br.com.ramires.gourment.coffeclient.ui.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.databinding.FragmentOrdersBinding
import br.com.ramires.gourment.coffeclient.ui.payment.PaymentMockActivity

class OrdersFragment(private val repository: OrderRepositoryInterface) : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var adapter: OrderAdapter
    private lateinit var paymentResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val status = data?.getStringExtra("PAYMENT_STATUS")
                    val orderId = data?.getIntExtra("ORDER_ID", -1)

                    if (status != null && orderId != null) {
                        val order = viewModel.getOrderById(orderId)
                        handlePaymentStatus(status, order)
                    } else {
                        showError("Dados de pagamento inválidos.")
                    }
                }
            } catch (e: Exception) {
                showError("Erro ao processar o resultado do pagamento: ${e.message}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = OrderViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(OrderViewModel::class.java)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrderAdapter(
            onOrderClick = { orderId -> viewModel.expandOrder(orderId) },
            onOrderSave = { updatedOrder, onOrderSaved ->
                viewModel.updateOrder(updatedOrder)
                viewModel.loadOrders { onOrderSaved() }
            },
            onPaymentRequest = { order ->
                val intent = Intent(requireContext(), PaymentMockActivity::class.java).apply {
                    putExtra("ORDER_ID", order.id)
                }
                paymentResultLauncher.launch(intent)
            }
        )
        binding.recyclerViewOrders.adapter = adapter

        viewModel.orders.value?.let {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun observeViewModel() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            adapter.submitList(orders)
            adapter.notifyDataSetChanged()

            // Expande automaticamente o pedido com status CARRINHO
            val cartOrderId = orders.find { it.status == OrderStatus.CARRINHO.toString() }?.id
            if (cartOrderId != null && adapter.getExpandedOrderId() == null) {
                adapter.setExpandedOrder(cartOrderId)
            }
        }
    }

    private fun handlePaymentStatus(status: String, order: Order?) {
        if (order == null) {
            showError("Pedido não encontrado. Por favor, tente novamente.")
            return
        }

        when (status) {
            "success" -> {
                order.status = OrderStatus.NOVO.toString()
                viewModel.updateOrder(order)
                showSuccess("Pagamento efetuado com sucesso! Pedido finalizado.")
            }
            "error" -> {
                showError("Pagamento recusado. Tente novamente.")
            }
        }
    }

    private fun showSuccess(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("AVISO")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("ERRO")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
