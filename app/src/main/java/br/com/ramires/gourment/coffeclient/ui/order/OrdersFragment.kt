package br.com.ramires.gourment.coffeclient.ui.order

//TODO import android.content.Intent
//TODO import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.databinding.FragmentOrdersBinding

class OrdersFragment(private val repository: OrderRepositoryInterface) : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var adapter: OrderAdapter

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
            onOrderClick = { orderid -> viewModel.expandOrder(orderid) }
        ) { updatedOrder, onOrderSaved ->
            viewModel.updateOrder(updatedOrder)
            val newCartOrderId = viewModel.loadOrders()
            newCartOrderId?.let {
                adapter.setExpandedOrder(it) // Expande o pedido recém-criado
                onOrderSaved()
            }
        }
        binding.recyclerViewOrders.adapter = adapter

        viewModel.orders.value?.let {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        /*TODO
        viewModel.eventNavigateToPayment.observe(viewLifecycleOwner) { order ->
            order?.let {
                val paymentUrl = "https://pagamento.meiosdepagamento.com/pedido/${order.id}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
                startActivity(intent)
            }
        }
        */
    }

    private fun observeViewModel() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            adapter.submitList(orders)
            adapter.notifyDataSetChanged()

            /*
            // Expande automaticamente o primeiro pedido apenas ao carregar a lista
            if (orders.isNotEmpty() && adapter.getExpandedOrderId() == null) {
                adapter.setExpandedOrder(orders.firstOrNull()?.id)
            }
            */

            // Expande automaticamente o pedido com status CARRINHO
            val cartOrderId = orders.find { it.status == OrderStatus.CARRINHO.toString() }?.id
            if (cartOrderId != null && adapter.getExpandedOrderId() == null) {
                adapter.setExpandedOrder(cartOrderId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
