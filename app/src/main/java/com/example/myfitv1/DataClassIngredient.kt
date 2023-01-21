package com.example.myfitv1

import com.google.gson.annotations.SerializedName

class DataClassIngredient(
    @SerializedName("ingredientName") var ingredientName: String? = null,
    @SerializedName("ingredientQuantity") var ingredientQuantity: String? = null,
    @SerializedName("ingredientCarbs") var ingredientCarbs: String? = null,
    @SerializedName("ingredientProtein") var ingredientProtein: String? = null,
    @SerializedName("ingredientFat") var ingredientFat: String? = null,
    @SerializedName("ingredientImg") var ingredientImg: String? = null
)



