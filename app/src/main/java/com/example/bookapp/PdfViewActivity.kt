package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bookapp.databinding.ActivityPdfViewBinding
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.log

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    var bookId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent
        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get Pdf URL from db")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pdfUrl = snapshot.child("url").value
                Log.d(TAG, "onDataChange: PDF_URL: $pdfUrl")

                loadBookFromUrl("$pdfUrl")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Get Pdf from storage")

        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constants.MAX_BYTES_PDF).addOnSuccessListener { bytes ->
            Log.d(TAG, "loadBookFromUrl: got pdf from urf!")
            binding.pdfView.fromBytes(bytes).swipeHorizontal(true).pageSnap(true).pageFling(true)
                .onPageChange { page, pageCount ->
                    //set current page and total page
                    val currentPage = page + 1
                    binding.pageNumberTv.text = "$currentPage/$pageCount"
                }.onError { t ->
                    Log.d(TAG, "loadBookFromUrl: ${t.message}")
                }.onPageError { page, t ->
                    Log.d(TAG, "loadBookFromUrl: ${t.message}")
                }.load()
            binding.progressBar.visibility = View.GONE
        }.addOnFailureListener { e ->
            Log.d(TAG, "loadBookFromUrl: Failed to get url due to ${e.message}")
            binding.progressBar.visibility = View.GONE
        }
    }
}