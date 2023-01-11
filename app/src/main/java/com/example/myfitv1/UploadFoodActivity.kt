package com.example.myfitv1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitv1.databinding.ActivityUploadFoodBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class UploadFoodActivity : AppCompatActivity() {

    /**
     * Upload an ingredient to the database with Name, Img and Uri
     */


    private lateinit var binding: ActivityUploadFoodBinding
    private var imageURL: String? = null
    private var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data!!.data
                binding.uploadImage.setImageURI(uri)
            } else {
                Toast.makeText(this@UploadFoodActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }
        binding.uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.submitIngredientBTN.setOnClickListener {
            saveData()
        }


    }

    private fun saveData() {
        if (uri == null) {
            Toast.makeText(this@UploadFoodActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
            return
        } else {
            val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
                .child(uri!!.lastPathSegment!!)
            val builder = AlertDialog.Builder(this@UploadFoodActivity)
            builder.setCancelable(false)
            builder.setView(R.layout.progress_layout)
            val dialog = builder.create()
            dialog.show()
            storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                val urlImage = uriTask.result
                imageURL = urlImage.toString()
                uploadData()
                dialog.dismiss()
            }.addOnFailureListener {
                dialog.dismiss()
            }
        }
    }




    private fun uploadData(){
        val title = binding.uploadIngredient.text.toString().lowercase(Locale.ROOT)
        val quantity = binding.uploadAmount.text.toString()
        val carbs = binding.uploadCarbs.text.toString()
        val protein = binding.uploadProtein.text.toString()
        val fat = binding.uploadFat.text.toString()
        val dataClass = DataClassIngredient(title, quantity, carbs, protein, fat, imageURL)

        FirebaseDatabase.getInstance().getReference("Ingredients")
            .push()
            .setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@UploadFoodActivity, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@UploadFoodActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

}