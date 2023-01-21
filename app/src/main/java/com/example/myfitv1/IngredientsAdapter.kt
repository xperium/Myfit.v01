package com.example.myfitv1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * This is the IngredientsAdapter class of the app, which is used to display ingredients in a RecyclerView.
 * The class takes a list of DataClassIngredient objects as a parameter in its constructor.
 * The IngredientsAdapter class extends the RecyclerView.Adapter class and overrides its onCreateViewHolder, onBindViewHolder, and getItemCount methods.

 * onCreateViewHolder method is called when a new view holder is needed, it inflates the ingredient_rec_view layout, which is a layout for a single item in the RecyclerView, and returns an instance of ViewHolder class.
 * onBindViewHolder method binds the data to the item in the RecyclerView. It takes a ViewHolder instance and the position of the item in the list as its parameters. This method updates the text and image of each view holder according to the data of the corresponding ingredient in the list. It also sets an onClickListener on the layout of each item, so when a user clicks on an item in the RecyclerView it is removed from the list and the RecyclerView is updated.
 * getItemCount() method returns the number of items in the list of ingredients.
 * This adapter is responsible for updating the RecyclerView with the ingredients selected from the database with the Name, Image and Layout of the ingredient.
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
        Log.d("IngredientsAdapter", "Binding ingredient data: $ingredient")
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
