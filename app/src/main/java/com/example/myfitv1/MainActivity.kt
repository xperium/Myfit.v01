    package com.example.myfitv1

    import android.annotation.SuppressLint
    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.text.Editable
    import android.text.TextWatcher
    import android.widget.AdapterView
    import android.widget.ArrayAdapter
    import android.widget.AutoCompleteTextView
    import android.widget.Button
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.myfitv1.databinding.ActivityMainBinding
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener

    class MainActivity : AppCompatActivity() {


        /**
         * Updates the recyclerview with the ingredients selected from the database
         * Then makes a new list for the user, so he has his unique list in the database that used as a shopping list
         * When an item is removed from the shopping list it is added to the food stock of the user
         * The food-stock(Ingredients) is then compared to the the recipes from the database, and the user gets recipes based on the food-stock(Ingredients)
         */

        // Declaring an instance of FirebaseAuth
        private lateinit var auth: FirebaseAuth

        // Declaring a Button to sign out the user
        private lateinit var signOutBtn: Button

        // List to store selected ingredients
        private var ingredientsList = mutableListOf<DataClassIngredient>()

        // View binding for main activity layout
        private lateinit var binding: ActivityMainBinding

        // Utility object to query ingredients from Firebase database
       private val firebaseUtility = FirebaseUtility()

        // List to hold ingredients later
        private val ingredients = arrayOf("Ingredient 1", "Ingredient 2", "Ingredient 3")

        val ingredientsAdapter = IngredientsAdapter(ingredientsList)

        override fun onPause() {
            super.onPause()
            auth = FirebaseAuth.getInstance()
            // Get the current user's unique ID
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_value"

            if (userId != null) {
                // Use userId here
            } else {
                // currentUser is null, do something else
            }
            // Save the ingredients list to a node named after the user's unique ID
            val database = FirebaseDatabase.getInstance().reference
            database.child("ingredients_list").child(userId).setValue(ingredientsList)
        }

        override fun onResume() {
            super.onResume()
            auth = FirebaseAuth.getInstance()
            // Get the current user's unique ID
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_value"
            // Retrieve the ingredients list from a node named after the user's unique ID
            val database = FirebaseDatabase.getInstance().reference
            val ingredientsListRef = database.child("ingredients_list").child(userId)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    ingredientsList.clear()
                    for (ingredientSnapshot in dataSnapshot.children) {
                        val ingredient = ingredientSnapshot.getValue(DataClassIngredient::class.java)
                        if (ingredient != null) {
                            ingredientsList.add(ingredient)
                        }
                    }

                    // Update the RecyclerView's adapter with the retrieved ingredientsList
                    val recyclerView = findViewById<RecyclerView>(R.id.ingredients_recyclerview)
                    recyclerView.adapter = ingredientsAdapter
                    recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 3)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            ingredientsListRef.addValueEventListener(valueEventListener)
        }




        // Called when the activity is first created
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            auth = FirebaseAuth.getInstance()
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)




            // Getting a reference to the sign out button
            signOutBtn = findViewById(R.id.signOutBtn)

            // Setting up the signOutBtn click listener
            signOutBtn.setOnClickListener {
                // Sign out the user
                auth.signOut()

                // Go to the login screen
                val intent = Intent(this, LoginMobile::class.java)
                startActivity(intent)
                finish()
            }

            // Setting click listener for FAB to open UploadFoodActivity
            binding.fab.setOnClickListener{
                val intent = Intent(this, UploadFoodActivity::class.java)
                startActivity(intent)
            }



            // Setting up autocomplete adapter and attach it to the AutoCompleteTextView
            val autocompleteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)
            val ingredientAutocomplete = findViewById<AutoCompleteTextView>(R.id.ingredient_autocomplete)
            ingredientAutocomplete.setAdapter(autocompleteAdapter)



            // Setting up text change listener for the AutoCompleteTextView to query the Firebase database for matching ingredients
            ingredientAutocomplete.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    firebaseUtility.queryIngredients(s.toString(), autocompleteAdapter)

                }
            })

            val recyclerView = findViewById<RecyclerView>(R.id.ingredients_recyclerview)


            // Set up item click listener for the AutoCompleteTextView to add selected ingredient to the ingredients list and update the RecyclerView
            ingredientAutocomplete.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedIngredient = autocompleteAdapter.getItem(position) as String
                ingredientAutocomplete.setText("")
                if (updatedIngredients.contains(selectedIngredient)) {
                    val query = FirebaseDatabase.getInstance().reference.child("Ingredients")
                        .orderByChild("ingredientName")
                        .equalTo(selectedIngredient)
                    // Query the Firebase database for the selected ingredient and add it to the ingredients list
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val ingredientsAdapter = ingredientsAdapter.notifyDataSetChanged()
                            for (ingredientSnapshot in dataSnapshot.children) {
                                val ingredient = ingredientSnapshot.getValue(DataClassIngredient::class.java)
                                // Update the list of ingredients with the selected ingredient
                                if (ingredient != null) {
                                    ingredientsList.add(ingredient)
                                    ingredientsAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                } else {
                    // If the selected item does not exist in the updatedIngredients list, add the search string to the list
                    val searchString = ingredientAutocomplete.text.toString()
                    if (!searchString.isEmpty()) {
                        ingredientsList.add(DataClassIngredient(ingredientName = searchString))
                        ingredientsAdapter.notifyDataSetChanged()
                    }
                }

            }






            // Set up RecyclerView for displaying selected ingredients
            val gridLayoutManager = GridLayoutManager(this, 3)
            val ingredientsAdapter = IngredientsAdapter(ingredientsList)
            recyclerView.adapter = ingredientsAdapter
            recyclerView.layoutManager = gridLayoutManager






        }
    }