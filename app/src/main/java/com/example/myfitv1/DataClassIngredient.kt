package com.example.myfitv1

class DataClassIngredient
    (
    /**
     *  Just the data class for the food-stock(Ingredients) that is compared to the the recipes from the database, and the user gets recipes based on the food-stock(Ingredients).
     */

    var ingredientName: String? = null,
    private var ingredientQuantity: String? = null,
    private var ingredientCarbs: String? = null,
    private var ingredientProtein: String? = null,
    private var ingredientFat: String? = null,
    var ingredientImg: String? = null
)


