package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookapp.databinding.ActivityBookDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding

    private var bookId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent
        bookId = intent.getStringExtra("bookId")!!

        loadBookDetails()

        //handle backbutton click
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle read book
        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this,PdfViewActivity::class.java)
            intent.putExtra("bookId", bookId)
            startActivity(intent)
        }
    }

    private fun loadBookDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get data
                val categoryId = "${snapshot.child("categoryId").value}"
                val author = "${snapshot.child("author").value}"
                val title = "${snapshot.child("title").value}"
                val url = "${snapshot.child("url").value}"
                val uid = "${snapshot.child("uid").value}"
                val viewCount = "${snapshot.child("viewCount").value}"
                val downloadCount = "${snapshot.child("downloadCount").value}"
                val description = "${snapshot.child("description").value}"

                MyApplication.loadPdfFromUrlSinglePage(
                    "$url",
                    "$title",
                    binding.pdfView,
                    binding.progressBar,
                    binding.pagesTv
                )

                //set data
                binding.titleTv.text = title
                binding.descriptionTv.text = description
                binding.authorTv.text = author
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}