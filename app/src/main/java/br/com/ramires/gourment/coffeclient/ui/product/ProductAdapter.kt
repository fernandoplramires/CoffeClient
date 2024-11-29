package br.com.ramires.gourment.coffeclient.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.ramires.gourment.coffeclient.R
import br.com.ramires.gourment.coffeclient.data.model.Product
import br.com.ramires.gourment.coffeclient.databinding.ItemProductBinding
import br.com.ramires.gourment.coffeclient.util.Convertions
import br.com.ramires.gourment.coffeclient.util.ImageCache
import com.bumptech.glide.Glide

class ProductAdapter(private val onAction: (ActionType, Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    enum class ActionType {
        SAVE, REMOVE
    }

    private val productList = mutableListOf<Product>()
    private val imageCache = ImageCache()
    private val selectedProducts = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    fun submitList(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    fun getPositionById(id: Int): Int {
        return productList.indexOfFirst { it.id == id }
    }

    fun setSelectedProducts(productIds: List<Int>) {
        selectedProducts.clear()
        selectedProducts.addAll(productIds)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            // Preenche os dados do produto
            binding.textViewName.text = product.name
            binding.textViewDescription.text = product.description
            binding.textViewPrice.text = Convertions.formatToBrazilianCurrency((product.price ?: 0.0))

            // Carregar imagem (usando cache)
            val imageUri = product.imageUrl?.let { imageCache.getCachedImage(binding.root.context, it) }
            if (imageUri != null) {
                //Imagem obtida do cache
                Glide.with(binding.root.context).load(imageUri).into(binding.imageViewProduct)
            } else {
                // Imagem padrão
                binding.imageViewProduct.setImageResource(R.drawable.ic_placeholder)
            }

            // Verifica se o produto está selecionado
            val isSelected = selectedProducts.contains(product.id)

            // Define a borda com base no estado de seleção
            val borderDrawable = if (isSelected) R.drawable.item_shadow_border_red else R.drawable.item_shadow_border
            binding.productContainer.setBackgroundResource(borderDrawable)

            binding.productContainer.setOnClickListener {
                if (isSelected) {
                    selectedProducts.remove(product.id)
                    onAction(ActionType.REMOVE, product)
                } else {
                    product.id?.let { selectedProducts.add(it) }
                    onAction(ActionType.SAVE, product)
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
