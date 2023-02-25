    package com.example.myfitv1


    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.text.Editable
    import android.text.TextWatcher
    import android.util.Log
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
         *
         * Firebase as a backend, and makes use of the FirebaseAuth and FirebaseDatabase classes to handle user authentication and data storage. The app is using the activity's lifecycle methods, onPause and onResume, to save and retrieve a list of ingredients that is unique to the currently signed-in user. The onPause method saves the ingredients list to a node in the Firebase database named after the user's unique ID, and the onResume method retrieves the ingredients list from the same node and updates the RecyclerView's adapter with the retrieved ingredients.
         * It contains a RecyclerView and autocompleteTextView to search and add ingredients and user can see the ingredients list and use it as shopping list.
         * TODO: The food-stock(Ingredients) is then compared to the the recipes from the database, and the user gets recipes based on the food-stock(Ingredients)
         */

        // Declaring an instance of FirebaseAuth
        private lateinit var auth: FirebaseAuth

        // Declaring a Button to sign out the user
        private lateinit var signOutBtn: Button

        // Declaring a Button to take user to workout page
        private lateinit var goToWorkout: Button

        // List to store selected ingredients
        private var ingredientsList = mutableListOf<DataClassIngredient>()

        // View binding for main activity layout
        private lateinit var binding: ActivityMainBinding

        // Utility object to query ingredients from Firebase database
       private val firebaseUtility = FirebaseUtility()



        var ingredientsAdapter = IngredientsAdapter(ingredientsList)

        override fun onPause() {
            super.onPause()
            auth = FirebaseAuth.getInstance()
            // Get the current user's unique ID
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_value"


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
            Log.d("MainActivity", "Retrieved ingredients list with size: ${ingredientsList.size}")
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    ingredientsList.clear()
                    for (ingredientSnapshot in dataSnapshot.children) {
                        val ingredient = ingredientSnapshot.getValue(DataClassIngredient::class.java)
                        if (ingredient != null) {
                            ingredientsList.add(ingredient)
                            Log.d("IngredientsList", ingredientsList.toString())

                        }
                    }

                    // Update the RecyclerView's adapter with the retrieved ingredientsList
                    val recyclerView = findViewById<RecyclerView>(R.id.ingredients_recyclerview)
                    recyclerView.adapter = ingredientsAdapter
                    recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 3)
                    Log.d("IngredientsList", ingredientsList.toString())

                    if (ingredientsList.isNotEmpty()) {
                        Log.d("IngredientsList", ingredientsList.toString())
                        // Save the ingredients list to a node named after the user's unique ID
                        val databaseI = FirebaseDatabase.getInstance().reference
                        databaseI.child("ingredients_list").child(userId).setValue(ingredientsList)
                    }

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            ingredientsListRef.addValueEventListener(valueEventListener)
            if (ingredientsList.isNotEmpty()) {
                Log.d("IngredientsList", ingredientsList.toString())
                // Save the ingredients list to a node named after the user's unique ID
                val databaseI = FirebaseDatabase.getInstance().reference
                databaseI.child("ingredients_list").child(userId).setValue(ingredientsList)
            }
        }




        // Called when the activity is first created
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            auth = FirebaseAuth.getInstance()
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Getting a reference to the sign out button
            goToWorkout = findViewById(R.id.goToWorkout)


            goToWorkout.setOnClickListener {

                // Go to the Workout screen
                val intent = Intent(this, WorkoutMain::class.java)
                startActivity(intent)
                finish()
            }

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

            val updatedIngredients = mutableListOf<String>()

            // Setting up autocomplete adapter and attaching it to the AutoCompleteTextView
            val autocompleteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, updatedIngredients)
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


            // Setting up item click listener for the AutoCompleteTextView to add selected ingredient to the ingredients list and update the RecyclerView
            ingredientAutocomplete.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedIngredient = autocompleteAdapter.getItem(position)
                ingredientAutocomplete.setText("")
                val query = FirebaseDatabase.getInstance().reference.child("Ingredients")
                    .orderByChild("ingredientName")
                    .equalTo(selectedIngredient)
                // Query the Firebase database for the selected ingredient and add it to the ingredients list
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // If the ingredient is not found in the database, create a new ingredient object
                            val newIngredient = DataClassIngredient(ingredientName = selectedIngredient)
                            ingredientsList.add(newIngredient)
                            ingredientsAdapter.notifyDataSetChanged()
                            // Save the ingredient to the current user's unique ingredients list in the Firebase database
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null) {
                                val database = FirebaseDatabase.getInstance().reference
                                newIngredient.ingredientName?.let {
                                    database.child("ingredients_list").child(userId).child(it).setValue(newIngredient)
                                }
                            }
                        } else {
                            for (ingredientSnapshot in dataSnapshot.children) {
                                val ingredient = ingredientSnapshot.getValue(DataClassIngredient::class.java)
                                Log.d("ingredient", ingredient.toString())
                                // Update the list of ingredients with the selected ingredient
                                if (ingredient != null) {
                                    ingredientsList.add(ingredient)
                                }
                            }
                            ingredientsAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            }












            // Set up RecyclerView for displaying selected ingredients
            val gridLayoutManager = GridLayoutManager(this, 3)
            ingredientsAdapter.notifyDataSetChanged()
            recyclerView.adapter = ingredientsAdapter
            recyclerView.layoutManager = gridLayoutManager






        }
    }