package com.example.myfitv1

import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class FirebaseUtility {

    /**
     * Queries the Firebase database for ingredients matching the search string and updates the autocomplete adapter with the results.
     *
     * @param searchString The string to search for in the database.
     * @param autocompleteAdapter The adapter to update with the search results.
     * @return A list of ingredients matching the search string.
     */


    fun queryIngredients(searchString: String, autocompleteAdapter: ArrayAdapter<String>): List<String> {
        val lowerCaseSearchString = searchString.lowercase(Locale.getDefault())
        val updatedIngredients = mutableListOf<String>()

        val query = FirebaseDatabase.getInstance().reference.child("Ingredients")
            .orderByChild("ingredientName")
            .startAt(lowerCaseSearchString)
            .endAt(lowerCaseSearchString + "\uf8ff")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ingredientSnapshot in dataSnapshot.children) {
                    val ingredient = ingredientSnapshot.getValue(DataClassIngredient::class.java)
                    if (ingredient?.ingredientName != null) {
                        val lowerCaseIngredientName = ingredient.ingredientName!!.lowercase(Locale.getDefault())
                        if (lowerCaseIngredientName.startsWith(lowerCaseSearchString)) {
                            updatedIngredients.add(ingredient.ingredientName!!)
                        }
                    }
                }
                if (!updatedIngredients.contains(searchString)) {
                    updatedIngredients.add(searchString)
                }
                autocompleteAdapter.clear()
                for (ingredient in updatedIngredients) {
                    autocompleteAdapter.add(ingredient)
                }
                autocompleteAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return updatedIngredients
    }


}
