package br.com.ramires.gourment.coffeclient.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.ramires.gourment.coffeclient.R
import br.com.ramires.gourment.coffeclient.data.model.Product
import br.com.ramires.gourment.coffeclient.databinding.ItemProductBinding
import br.com.ramires.gourment.coffeclient.util.Convertions
import br.com.ramires.gourment.coffeclient.util.ImageCache
import com.bumptech.glide.Glide

class ProductAdapter(private val onAction: (ActionType, Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    enum class ActionType {
        EDIT, REMOVE, SAVE, UPLOAD_IMAGE
    }

    private val productList = mutableListOf<Product>()
    private val imageCache = ImageCache()

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

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            // Preenche os dados do produto
            binding.textViewName.text = product.name
            binding.textViewDescription.text = product.description
            binding.textViewPrice.text = "${Convertions.formatToBrazilianCurrency((product?.price ?: 0.0))}"
            binding.editTextName.setText(product.name)
            binding.editTextDescription.setText(product.description)
            binding.editTextPrice.setText(product.price.toString())

            // Carregar imagem (usando cache)
            val imageUri = product.imageUrl?.let { imageCache.getCachedImage(binding.root.context, it) }
            if (imageUri != null) {
                //Imagem obtida do cache
                Glide.with(binding.root.context).load(imageUri).into(binding.imageViewProduct)
            } else {
                // Imagem padrão
                binding.imageViewProduct.setImageResource(R.drawable.ic_placeholder)
            }

            // Altera a borda do contêiner baseado no estado
            binding.productContainer.setBackgroundResource(R.drawable.item_shadow_border)

            // Configuração de eventos (editar)
            binding.buttonEdit.setOnClickListener {
                toggleEditing(enabled = true)
                onAction(ActionType.EDIT, product)
            }

            // Configuração de eventos (salvar)
            binding.buttonSave.setOnClickListener {
                toggleEditing(enabled = false)
                val updatedProduct = product.copy(
                    name = binding.editTextName.text.toString(),
                    description = binding.editTextDescription.text.toString(),
                    price = binding.editTextPrice.text.toString().toDoubleOrNull() ?: product.price,
                    imageUrl = product.imageUrl
                )
                onAction(ActionType.SAVE, updatedProduct)
                Toast.makeText(binding.root.context, "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            }

            // Configuração de eventos (remover)
            binding.buttonRemove.setOnClickListener {
                onAction(ActionType.REMOVE, product)
            }

            // Configuração de eventos (imagem)
            binding.buttonUploadImage.setOnClickListener {
                onAction(ActionType.UPLOAD_IMAGE, product)
            }
        }

        private fun toggleEditing(enabled: Boolean) {

            binding.textViewName.visibility = if (enabled) View.GONE else View.VISIBLE
            binding.editTextName.visibility = if (enabled) View.VISIBLE else View.GONE

            binding.textViewDescription.visibility = if (enabled) View.GONE else View.VISIBLE
            binding.editTextDescription.visibility = if (enabled) View.VISIBLE else View.GONE

            binding.textViewPrice.visibility = if (enabled) View.GONE else View.VISIBLE
            binding.editTextPrice.visibility = if (enabled) View.VISIBLE else View.GONE

            binding.buttonSave.isEnabled = enabled
            binding.buttonEdit.isEnabled = !enabled
            binding.buttonUploadImage.visibility = if (enabled) View.VISIBLE else View.GONE

            // Atualizar borda do container
            val borderDrawable = if (enabled) R.drawable.item_shadow_border_red else R.drawable.item_shadow_border
            binding.productContainer.setBackgroundResource(borderDrawable)
        }
    }
}


