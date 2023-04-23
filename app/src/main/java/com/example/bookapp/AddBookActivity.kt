package com.example.bookapp

import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bookapp.databinding.ActivityAddBookBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.log

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    private var pdfUri: Uri? = null

    private val TAG = "PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

        binding.bookCategoryTv.setOnClickListener {
            categoryPickDialog()
        }

        binding.bookPdfBtn.setOnClickListener {
            pdfPickIntent()
        }

        binding.uploadBtn.setOnClickListener {
            validateData()
        }
    }

    private var title = " "
    private var category = " "
    private var author = " "
    private var description = " "

    private fun validateData() {
        Log.d(TAG, "validateData: Validating data")

        title = binding.bookTitleEt.text.toString().trim()
        category = binding.bookCategoryTv.text.toString().trim()
        author = binding.bookAuthorEt.text.toString().trim()
        description = binding.bookDescriptionEt.text.toString().trim()

        if (title.isEmpty() || category.isEmpty() || author.isEmpty() || description.isEmpty() || pdfUri == null) {
            Toast.makeText(this, "Missing field value...", Toast.LENGTH_SHORT).show()
        } else {
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: uploading to storage...")

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Books/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!).addOnSuccessListener { taskSnapshot ->
            Log.d(TAG, "uploadPdfToStorage: Pdf uploaded now getting url...")
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val uploadedPdfUrl = "${uriTask.result}"

            uploadedPdfInfoToDb(uploadedPdfUrl, timestamp)
        }.addOnFailureListener { e ->
            Log.d(TAG, "uploadPdfToStorage: Failed to upload due to ${e.message}")
            Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadedPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadedPdfInfoToDb: Uploading to db")

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["author"] = "$author"
        hashMap["description"] = "$description"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = "$timestamp"
        hashMap["viewCount"] = 0
        hashMap["downloadsCount"] = 0
        hashMap["rating"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp").setValue(hashMap).addOnSuccessListener {
            Log.d(TAG, "uploadedPdfInfoToDb: Uploaded")
            Toast.makeText(this, "uploaded", Toast.LENGTH_SHORT).show()
            pdfUri = null
        }.addOnFailureListener { e ->
            Log.d(TAG, "uploadedPdfInfoToDb: failed to upload")
            Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories")
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var selectedCategoryId = " "
    private var selectedCategoryTitle = " "

    private fun categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: Showing pdf category pick dialogue")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices) {
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category").setItems(categoriesArray) { dialog, which ->
            selectedCategoryTitle = categoryArrayList[which].category
            selectedCategoryId = categoryArrayList[which].id

            binding.bookCategoryTv.text = selectedCategoryTitle

            Log.d(TAG, "categoryPickDialog: Selected Category ID: $selectedCategoryId")
            Log.d(TAG, "categoryPickDialog: Selected Category Title: $selectedCategoryTitle")
        }.show()
    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "PDF Picked: ")
                pdfUri = result.data!!.data
            } else {
                Log.d(TAG, "PDF Pick cancelled: ")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}