package com.example.bookapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bookapp.adapter.AdapterComment
import com.example.bookapp.databinding.ActivityBookDetailBinding
import com.example.bookapp.databinding.DialogAddCommentBinding
import com.example.bookapp.model.ModelComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var commentArrayList: ArrayList<ModelComment>
    private lateinit var adapterComment: AdapterComment
    private var bookId = ""
    private var isFavourite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent
        bookId = intent.getStringExtra("bookId")!!

        //progress bar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            checkFavourite()
        }

        loadBookDetails()
        showComments()

        //handle backbutton click
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle read book
        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("bookId", bookId)
            startActivity(intent)
        }

        binding.listenBookBtn.setOnClickListener {
            val intent = Intent(this, AudioBookActivity::class.java)
            intent.putExtra("bookId", bookId)
            startActivity(intent)
        }

        binding.reviewBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show()
            }
            else {
                addCommentDialog()
            }
        }

        binding.addFavouriteBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show()
            } else {
                if (isFavourite) {
                    removeFavourite()
                }
                else {
                    addFavorite()
                }
            }
        }
    }

    private fun addFavorite() {
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["bookId"] = bookId
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favourites").child(bookId)
            .setValue(hashMap).addOnSuccessListener {
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }

    }

    private fun removeFavourite() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favourites").child(bookId)
            .removeValue().addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed due to${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkFavourite() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favourites").child(bookId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isFavourite = snapshot.exists()
                    val filledFavourite = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.favourite_filled
                    )
                    val unfilledFavourite = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.favourite
                    )
                    if (isFavourite) {
                        //available in fav
                        binding.addFavouriteBtn.setImageDrawable(filledFavourite)
                    } else {
                        //not available in fav
                        binding.addFavouriteBtn.setImageDrawable(unfilledFavourite)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
    }

    private fun showComments() {
        //init arrayList
        commentArrayList = ArrayList()

        //db path to comments
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).child("Comments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelComment::class.java)
                    commentArrayList.add(model!!)
                }
                adapterComment = AdapterComment(this@BookDetailActivity, commentArrayList)
                binding.commentsRv.adapter = adapterComment
            }

            //setup adapter
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var comment = ""

    private fun addCommentDialog() {
        var addCommentBinding = DialogAddCommentBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(addCommentBinding.root)

        //create and show dialog
        val alertDialog = builder.create()
        alertDialog.show()

        //handle clicks
        addCommentBinding.cancelTv.setOnClickListener { alertDialog.dismiss() }
        addCommentBinding.postTv.setOnClickListener {
            //get data
            comment = addCommentBinding.commentEt.text.toString().trim()
            if (comment.isEmpty()) {
                Toast.makeText(this, "Write your comment first...", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                addComment()
            }
        }
    }

    private fun addComment() {
        //show progress
        progressDialog.setMessage("Adding Comment")
        progressDialog.show()

        //timestamp for comment
        val timestamp = "${System.currentTimeMillis()}"

        //setup data to db
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["bookId"] = "$bookId"
        hashMap["timestamp"] = "$timestamp"
        hashMap["comment"] = "$comment"
        hashMap["uid"] = "${firebaseAuth.uid}"

        //db path
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).child("Comments").child(timestamp).setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Comment Added...", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show()
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
                val audio = "${snapshot.child("audio").value}"
                val uid = "${snapshot.child("uid").value}"
                val thumbnail = "${snapshot.child("image").value}"
                val viewCount = "${snapshot.child("viewCount").value}"
                val downloadCount = "${snapshot.child("downloadCount").value}"
                val description = "${snapshot.child("description").value}"

                MyApplication.loadPdfThumbnail(
                    "$url", "$audio",
                    "$title", "$author", "$thumbnail", binding.pdfView, binding.progressBar
                )
                /**
                MyApplication.loadPdfFromUrlSinglePage(
                "$url",
                "$title",
                binding.pdfView,
                binding.progressBar,
                binding.pagesTv
                )
                 **/
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