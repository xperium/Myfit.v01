package com.example.myfitv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Updates the recyclerview with the ingredients selected from the database
 * With Name, Image and Layout
 */


class IngredientsAdapter(private val ingredients: MutableList<DataClassIngredient>) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientNameTextView = itemView.findViewById<TextView>(R.id.ingredient_name)!!
        val ingredientImageView = itemView.findViewById<ImageView>(R.id.ingredient_image)!!
        val ingredientLayout = itemView.findViewById<LinearLayout>(R.id.ingredient_layout)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_rec_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.ingredientNameTextView.text = ingredient.ingredientName
        Glide.with(holder.itemView.context)
            .load(ingredient.ingredientImg)
            .into(holder.ingredientImageView)
        holder.ingredientLayout.setOnClickListener {
            ingredients.remove(ingredient)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}
