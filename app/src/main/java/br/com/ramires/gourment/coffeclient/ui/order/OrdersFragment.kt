package br.com.ramires.gourment.coffeclient.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.ramires.gourment.coffeclient.data.repository.order.OrderRepositoryInterface
import br.com.ramires.gourment.coffeclient.databinding.FragmentOrdersBinding

class OrdersFragment(private val repository: OrderRepositoryInterface) : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var deviceId: String
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
            onOrderClick = { orderid -> viewModel.expandOrder(orderid) },
            onOrderSave  = { updatedOrder  -> viewModel.updateOrder(updatedOrder) }
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

            // Expande automaticamente o primeiro pedido apenas ao carregar a lista
            if (orders.isNotEmpty() && adapter.getExpandedOrderId() == null) {
                adapter.setExpandedOrder(orders.firstOrNull()?.id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
