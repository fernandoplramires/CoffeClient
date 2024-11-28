package br.com.ramires.gourment.coffeclient.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.ramires.gourment.coffeclient.R
import br.com.ramires.gourment.coffeclient.data.model.Order
import br.com.ramires.gourment.coffeclient.data.model.OrderStatus
import br.com.ramires.gourment.coffeclient.databinding.ItemOrderBinding
import br.com.ramires.gourment.coffeclient.util.Convertions

class OrderAdapter(
    private val onOrderClick: (Int) -> Unit,
    private val onOrderSave: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orders = mutableListOf<Order>()
    private var expandedOrderId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val isExpanded = order.id == expandedOrderId

        with(holder.binding) {

            // Exibe ou oculta os detalhes do pedido
            layoutOrderDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Altera a cor do fundo do título ao expandir
            textViewOrderTitle.setBackgroundResource(
                if (isExpanded) R.drawable.item_order_header_background_selected
                else R.drawable.item_order_header_background
            )

            // Titulo do pedido
            textViewOrderTitle.text = "${order.status} Pedido #${order.id}"

            // Preencher os itens do pedido dinamicamente
            layoutOrderItems.removeAllViews() // Limpar itens antigos
            order.details?.forEachIndexed  { index, detail ->
                val itemLayout = LayoutInflater.from(root.context)
                    .inflate(R.layout.item_order_detail, layoutOrderItems, false)

                val linearLayoutItemOrderDetail = itemLayout.findViewById<LinearLayout>(R.id.linearLayoutItemOrderDetail)
                val textViewItemName = itemLayout.findViewById<TextView>(R.id.textViewItemName)
                val textViewItemQuantity = itemLayout.findViewById<TextView>(R.id.textViewItemQuantity)
                val buttonIncrease = itemLayout.findViewById<Button>(R.id.buttonIncreaseQuantity)
                val buttonDecrease = itemLayout.findViewById<Button>(R.id.buttonDecreaseQuantity)
                val buttonRemove = itemLayout.findViewById<Button>(R.id.buttonRemoveItem)

                // Define uma cor alternada com base na posição
                val backgroundColor = if (index  % 2 == 0) {
                    ContextCompat.getColor(holder.itemView.context, R.color.color_odd)
                } else {
                    ContextCompat.getColor(holder.itemView.context, R.color.color_even)
                }

                // Aplica a cor de fundo no layout principal do item
                linearLayoutItemOrderDetail.setBackgroundColor(backgroundColor)

                // Configurar nome e quantidade
                textViewItemName.text = detail.productName
                textViewItemQuantity.text = detail.quantity.toString().plus("x -")

                // Configurar ações dos botões
                buttonIncrease.setOnClickListener {
                    // Lógica para aumentar quantidade
                    detail.quantity = detail.quantity?.plus(1)
                    textViewItemQuantity.text = detail.quantity.toString().plus("x -")
                }

                buttonDecrease.setOnClickListener {
                    // Lógica para diminuir quantidade
                    if (detail.quantity!! > 1) {
                        detail.quantity = detail.quantity!! - 1
                        textViewItemQuantity.text = detail.quantity.toString().plus("x -")
                    }
                }

                buttonRemove.setOnClickListener {
                    // Lógica para remover item
                    order.details = order.details?.filter { it != detail }
                    notifyDataSetChanged()
                }

                layoutOrderItems.addView(itemLayout)
            }

            // Valor total
            val precoFormatado = Convertions.formatToBrazilianCurrency(order.totalPrice?.toDouble() ?: 0.0)
            textViewTotalPrice.text = "Valor Total: ${precoFormatado}"

            // Informações do cliente
            textViewEmail.text = order.email ?: "Não informado"
            textViewPhone.text = order.phone ?: "Não informado"
            textViewZipCode.text = order.zipCode ?: "Não informado"
            textViewComplement.text = order.complement ?: "Não informado"
            textViewNumber.text = order.number ?: "Não informado"

            // Configuração inicial do Spinner
            val statusAdapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_spinner_item,
                OrderStatus.values().filter { it.name != "CARRINHO" }.map { it.name }
            )
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOrderStatus.adapter = statusAdapter
            val statusIndex = OrderStatus.values().indexOf(order.status ?: OrderStatus.NOVO)
            spinnerOrderStatus.setSelection(statusIndex)
            spinnerOrderStatus.isEnabled = false

            // Configuração inicial dos botões
            resetEditingState(this, holder.itemView.context)

            // Lógica do botão "Editar"
            buttonEditOrder.setOnClickListener {
                enableEditingFields(this, holder.itemView.context)
            }

            // Lógica do botão "Salvar"
            buttonSaveOrder.setOnClickListener {
                val updatedOrder = order.copy(
                    email = textViewEmail.text.toString(),
                    phone = textViewPhone.text.toString(),
                    zipCode = textViewZipCode.text.toString(),
                    complement = textViewComplement.text.toString(),
                    number = textViewNumber.text.toString(),
                    status = spinnerOrderStatus.selectedItem.toString()
                )
                onOrderSave(updatedOrder)
                resetEditingState(this, holder.itemView.context)
                Toast.makeText(root.context, "Pedido alterado com sucesso!", Toast.LENGTH_SHORT).show()
            }

            // Expande ou colapsa ao clicar no título
            textViewOrderTitle.setOnClickListener {
                expandedOrderId = if (isExpanded) null else order.id
                notifyDataSetChanged()
                //WARNING
                onOrderClick(order.id!!)
            }
        }
    }

    override fun getItemCount(): Int = orders.size

    fun submitList(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    private fun enableEditingFields(binding: ItemOrderBinding, context: Context) {
        binding.textViewEmail.isEnabled = true
        binding.textViewPhone.isEnabled = true
        binding.textViewZipCode.isEnabled = true
        binding.textViewComplement.isEnabled = true
        binding.textViewNumber.isEnabled = true
        binding.spinnerOrderStatus.isEnabled = true

        binding.buttonSaveOrder.isEnabled = true
        binding.buttonSaveOrder.setTextColor(ContextCompat.getColor(context, R.color.black))
        binding.buttonEditOrder.isEnabled = false
        binding.buttonEditOrder.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
    }

    private fun resetEditingState(binding: ItemOrderBinding, context: Context) {
        binding.textViewEmail.isEnabled = false
        binding.textViewPhone.isEnabled = false
        binding.textViewZipCode.isEnabled = false
        binding.textViewComplement.isEnabled = false
        binding.textViewNumber.isEnabled = false
        binding.spinnerOrderStatus.isEnabled = false

        binding.buttonSaveOrder.isEnabled = false
        binding.buttonSaveOrder.setTextColor(ContextCompat.getColor(context, R.color.light_gray))

        binding.buttonEditOrder.isEnabled = true
        binding.buttonEditOrder.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    inner class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)
}
