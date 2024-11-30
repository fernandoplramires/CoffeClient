package br.com.ramires.gourment.coffeclient.ui.order

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import br.com.ramires.gourment.coffeclient.util.Helpers
import br.com.ramires.gourment.coffeclient.util.Masks
import br.com.ramires.gourment.coffeclient.util.Validates

class OrderAdapter(
    private val onOrderClick: (Int) -> Unit,
    private val onOrderSave: (Order, () -> Unit) -> Unit
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
            textViewOrderTitle.text =
                if (order.status == OrderStatus.CARRINHO.toString()) "Pedido Atual"
                else "Pedido #${order.id}"

            // Preencher os itens do pedido dinamicamente
            layoutOrderItems.removeAllViews()
            if (order.details.isNullOrEmpty()) {
                // Se não houver itens no pedido, exibe uma mensagem ao usuário
                val emptyMessageView = LayoutInflater.from(root.context)
                    .inflate(R.layout.item_order_empty, layoutOrderItems, false)

                layoutOrderItems.addView(emptyMessageView)
            } else {
                order.details?.forEachIndexed { index, detail ->
                    val itemLayout = LayoutInflater.from(root.context)
                        .inflate(R.layout.item_order_detail, layoutOrderItems, false)

                    val linearLayoutItemOrderDetail =
                        itemLayout.findViewById<LinearLayout>(R.id.linearLayoutItemOrderDetail)
                    val textViewItemName = itemLayout.findViewById<TextView>(R.id.textViewItemName)
                    val textViewItemQuantity =
                        itemLayout.findViewById<TextView>(R.id.textViewItemQuantity)
                    val buttonIncrease =
                        itemLayout.findViewById<Button>(R.id.buttonIncreaseQuantity)
                    val buttonDecrease =
                        itemLayout.findViewById<Button>(R.id.buttonDecreaseQuantity)
                    val buttonRemove = itemLayout.findViewById<Button>(R.id.buttonRemoveItem)

                    // Define uma cor alternada com base na posição
                    val backgroundColor = if (index % 2 == 0) {
                        ContextCompat.getColor(holder.itemView.context, R.color.color_odd)
                    } else {
                        ContextCompat.getColor(holder.itemView.context, R.color.color_even)
                    }

                    // Aplica a cor de fundo no layout principal do item
                    linearLayoutItemOrderDetail.setBackgroundColor(backgroundColor)

                    // Configurar nome e quantidade
                    textViewItemName.text = detail.productName
                    textViewItemQuantity.text = detail.quantity.toString().plus("x -")

                    // Botao para aumentar quantidade
                    buttonIncrease.setOnClickListener {
                        detail.quantity = detail.quantity?.plus(1)
                        textViewItemQuantity.text = detail.quantity.toString().plus("x -")

                        // Atualiza o pedido e comunica o ViewModel
                        order.totalPrice = order.calculateTotalPrice()
                        onOrderSave(order) {
                            val position = holder.bindingAdapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                notifyItemChanged(position)
                            }
                        }
                    }

                    // Botao para diminuir quantidade
                    buttonDecrease.setOnClickListener {
                        if (detail.quantity!! > 1) {
                            detail.quantity = detail.quantity!! - 1
                            textViewItemQuantity.text = detail.quantity.toString().plus("x -")

                            // Atualiza o pedido e comunica o ViewModel
                            order.totalPrice = order.calculateTotalPrice()
                            onOrderSave(order) {
                                val position = holder.bindingAdapterPosition
                                if (position != RecyclerView.NO_POSITION) {
                                    notifyItemChanged(position)
                                }
                            }
                        }
                    }

                    // Botao para remover um item
                    buttonRemove.setOnClickListener {
                        order.details = order.details?.filter { it != detail }
                        onOrderSave(order) {
                            val position = holder.bindingAdapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                notifyItemChanged(position)
                            }
                        }
                        notifyDataSetChanged()
                    }

                    // Desabilita os botoes para itens que nao estao no carrinho
                    if (!order.status.equals(OrderStatus.CARRINHO.toString())) {
                        buttonIncrease.visibility = View.GONE
                        buttonDecrease.visibility = View.GONE
                        buttonRemove.visibility = View.GONE
                    } else {
                        buttonIncrease.visibility = View.VISIBLE
                        buttonDecrease.visibility = View.VISIBLE
                        buttonRemove.visibility = View.VISIBLE
                    }

                    layoutOrderItems.addView(itemLayout)
                }
            }

            // Valor total
            val precoFormatado = Convertions.formatToBrazilianCurrency(order.calculateTotalPrice())
            textViewTotalPrice.text = "Valor Total: ${precoFormatado}"

            // Habilita ou desabilita campos de edição com base no status
            val isEditable = order.status == OrderStatus.CARRINHO.toString()

            // Informações do cliente
            textViewEmail.text = Editable.Factory.getInstance().newEditable(order.email ?: "")
            textViewPhone.text = Editable.Factory.getInstance().newEditable(order.phone ?: "")
            textViewZipCode.text = Editable.Factory.getInstance().newEditable(order.zipCode ?: "")
            textViewComplement.text = Editable.Factory.getInstance().newEditable(order.complement ?: "")
            textViewNumber.text = Editable.Factory.getInstance().newEditable(order.number ?: "")

            // Mascaras aplicadas em CEP e Telefone
            Masks.applyCepMask(textViewZipCode)
            Masks.applyPhoneMask(textViewPhone)

            // Configura habilitação dos campos
            enableOrDisableEditingFields(isEditable, this)

            //Botao de Finalizar Pedido
            buttonCheckoutOrder.setOnClickListener {
                // Oculta o teclado antes de executar qualquer ação
                Helpers.hideKeyboard(it)

                // Validações utilizando a classe `Validates`
                val emailError = when {
                    textViewEmail.text.isNullOrBlank() -> "O campo E-mail é obrigatório."
                    !Validates.isEmail(textViewEmail.text.toString()) -> "Formato de E-mail inválido."
                    else -> null
                }

                val phoneError = when {
                    textViewPhone.text.isNullOrBlank() -> "O campo Telefone é obrigatório."
                    !Validates.isPhone(textViewPhone.text.toString()) -> "Formato de Telefone inválido."
                    else -> null
                }

                val zipCodeError = when {
                    textViewZipCode.text.isNullOrBlank() -> "O campo CEP é obrigatório."
                    !Validates.isCep(textViewZipCode.text.toString()) -> "Formato de CEP inválido. Ex: 12345-678."
                    !Validates.isValidZipCode(textViewZipCode.text.toString()) -> "CEP fora do raio de cobertura."
                    else -> null
                }

                val complementError = if (textViewComplement.text.isNullOrBlank()) "O campo Complemento é obrigatório." else null
                val numberError = if (textViewNumber.text.isNullOrBlank()) "O campo Número é obrigatório." else null

                // Exibir erros nos campos correspondentes
                textViewEmailError.text = emailError
                textViewEmailError.visibility = if (emailError != null) View.VISIBLE else View.GONE

                textViewPhoneError.text = phoneError
                textViewPhoneError.visibility = if (phoneError != null) View.VISIBLE else View.GONE

                textViewZipCodeError.text = zipCodeError
                textViewZipCodeError.visibility = if (zipCodeError != null) View.VISIBLE else View.GONE

                textViewComplementError.text = complementError
                textViewComplementError.visibility = if (complementError != null) View.VISIBLE else View.GONE

                textViewNumberError.text = numberError
                textViewNumberError.visibility = if (numberError != null) View.VISIBLE else View.GONE

                // Verifica se todos os campos estão válidos
                if (emailError == null && phoneError == null && zipCodeError == null && complementError == null && numberError == null) {
                    val updatedOrder = order.copy(
                        email = textViewEmail.text.toString(),
                        phone = textViewPhone.text.toString(),
                        zipCode = textViewZipCode.text.toString(),
                        complement = textViewComplement.text.toString(),
                        number = textViewNumber.text.toString(),
                        status = OrderStatus.NOVO.toString()
                    )

                    // Chamar onOrderSave e expandir o novo pedido
                    onOrderSave(updatedOrder) {
                        Toast.makeText(root.context, "Pedido #${updatedOrder.id} gerado", Toast.LENGTH_SHORT).show()

                        // Atualizar e expandir o novo pedido
                        //setExpandedOrder(updatedOrder.id)
                        expandedOrderId = updatedOrder.id
                        val position = holder.bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            notifyItemChanged(position)
                        }                    }
                }
            }

            // Expande ou colapsa ao clicar no título
            textViewOrderTitle.setOnClickListener {
                expandedOrderId = if (isExpanded) null else order.id
                notifyDataSetChanged()
                onOrderClick(order.id!!)
            }
        }
    }

    fun getExpandedOrderId(): Int? {
        return expandedOrderId
    }

    fun setExpandedOrder(orderId: Int?) {
        expandedOrderId = orderId
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = orders.size

    fun submitList(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    private fun enableOrDisableEditingFields(isEditable: Boolean, binding: ItemOrderBinding) {
        binding.textViewEmail.isFocusableInTouchMode = isEditable
        binding.textViewPhone.isFocusableInTouchMode = isEditable
        binding.textViewZipCode.isFocusableInTouchMode = isEditable
        binding.textViewComplement.isFocusableInTouchMode = isEditable
        binding.textViewNumber.isFocusableInTouchMode = isEditable

        binding.textViewEmail.isEnabled = isEditable
        binding.textViewPhone.isEnabled = isEditable
        binding.textViewZipCode.isEnabled = isEditable
        binding.textViewComplement.isEnabled = isEditable
        binding.textViewNumber.isEnabled = isEditable

        binding.buttonCheckoutOrder.isEnabled = isEditable
        binding.buttonCheckoutOrder.setTextColor(
            if (isEditable) ContextCompat.getColor(binding.root.context, R.color.black)
            else ContextCompat.getColor(binding.root.context, R.color.light_gray)
        )
    }

    inner class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)
}
