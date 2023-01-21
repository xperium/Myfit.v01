package com.example.myfitv1

import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class FirebaseUtility {

    /**
     * Provides methods for querying the Firebase database for ingredients.
     * The queryIngredients method takes in a searchString, and autocompleteAdapter as its parameter.
     * It first converts the searchString to lowercase, then it creates a query to Firebase database to retrieve ingredients that match the search string.
     * The method uses the orderByChild method to sort the ingredients by their ingredientName property, and uses the startAt and endAt methods to search for ingredients whose ingredientName properties start with the search string.
     * The method adds a ValueEventListener to the query, which gets triggered when the query finishes. The onDataChange method of the listener is called with a DataSnapshot of the query result. It loops through the children of the snapshot and if the ingredient matches the searchString, it will add it to the updatedIngredients list. Then it notifies the autocomplete adapter that the list has been updated.
     * This FirebaseUtility class is responsible for querying ingredients from the database and updating the autocomplete adapter with the search results.

     *
     * @param searchString The string to search for in the database.
     * @param autocompleteAdapter The adapter to update with the search results.
     * @param ingredientsList The list to update with the search results.
     * @param ingredientsAdapter The adapter for recyclerview that holds the list ingredients
     */


    fun queryIngredients(searchString: String, autocompleteAdapter: ArrayAdapter<String>): List<String> {
        val lowerCaseSearchString = searchString.lowercase(Locale.getDefault())
        val updatedIngredients = ArrayList<String>()
        updatedIngredients.add(searchString)

        updatedIngredients.add(searchString)  // add searchString to the autocomplete adapter before performing the Firebase query
        autocompleteAdapter.notifyDataSetChanged()

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

                // remove the searchString from the list of updated ingredients if it's not present in the Firebase Database
                if (!updatedIngredients.contains(searchString)) {
                    updatedIngredients.remove(searchString)
                }

                autocompleteAdapter.clear()
                autocompleteAdapter.addAll(updatedIngredients)
                autocompleteAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return updatedIngredients
    }



}
