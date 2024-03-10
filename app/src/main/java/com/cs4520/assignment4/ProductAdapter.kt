package com.cs4520.assignment4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ProductAdapter(private val productList: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]
        holder.bind(currentItem)
    }

    fun updateData(newProductList: List<Product>) {
        productList.clear()
        productList.addAll(newProductList)
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productTypeImage: ShapeableImageView =
            itemView.findViewById(R.id.product_type_image)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)
        private val expiryDate: TextView = itemView.findViewById(R.id.expiry_date)
        private val listItemRawLayout: ConstraintLayout =
            itemView.findViewById(R.id.list_item_raw_layout)

        fun bind(product: Product) {
            productName.text = product.name.toString()
            if (product.type == ProductType.Equipment) {
                productTypeImage.setImageResource(R.drawable.tools)
                listItemRawLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.red
                    )
                )
            } else {
                productTypeImage.setImageResource(R.drawable.food)
                listItemRawLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.yellow
                    )
                )
            }

            if (product.expiryDate != null) {
                expiryDate.text = "Expiry : ${product.expiryDate.toString()}"
                productPrice.text = "$${product.price.toString()}"
            } else {
                expiryDate.text = ""
                productPrice.text = "$${product.price.toString()}"
            }
        }
    }
}
