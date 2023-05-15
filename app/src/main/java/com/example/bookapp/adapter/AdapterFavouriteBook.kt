package com.example.bookapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.BookDetailActivity
import com.example.bookapp.MyApplication
import com.example.bookapp.R
import com.example.bookapp.databinding.RowFavouriteBinding
import com.example.bookapp.model.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterFavouriteBook : RecyclerView.Adapter<AdapterFavouriteBook.HolderFavouriteBook> {

    private val context: Context
    private var bookArrayList: ArrayList<ModelPdf>

    private lateinit var binding: RowFavouriteBinding

    constructor(context: Context, bookArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.bookArrayList = bookArrayList
    }

    inner class HolderFavouriteBook(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail = itemView.findViewById<ImageView>(R.id.pdfThumbnail)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
        val title = itemView.findViewById<TextView>(R.id.titleTv)
        val author = itemView.findViewById<TextView>(R.id.authorTv)
        val rating = itemView.findViewById<RatingBar>(R.id.ratingBar)
        val horizontalProgressBar = itemView.findViewById<ProgressBar>(R.id.progressBarHorizontal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFavouriteBook {
        val binding = RowFavouriteBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderFavouriteBook(binding.root)
    }

    override fun onBindViewHolder(holder: HolderFavouriteBook, position: Int) {
        val binding = RowFavouriteBinding.bind(holder.itemView)
        val model = bookArrayList[position]
        loadBookDetails(model, binding)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("bookId", model.id)
            context.startActivity(intent)
        }
    }

    private fun loadBookDetails(model: ModelPdf, binding: RowFavouriteBinding) {
        val bookId = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryId = "${snapshot.child("categoryId").value}"
                val author = "${snapshot.child("author").value}"
                val title = "${snapshot.child("title").value}"
                val url = "${snapshot.child("url").value}"
                val audio = "${snapshot.child("audio").value}"
                val uid = "${snapshot.child("uid").value}"
                val thumbnail = "${snapshot.child("image").value}"
                val description = "${snapshot.child("description").value}"

                //set favourite
                model.isFavourite = true
                model.title = title
                model.uid = uid
                model.url = url
                model.categoryId = categoryId
                model.author = author

                MyApplication.loadPdfThumbnail(
                    "$url", "$audio",
                    "$title", "$author", "$thumbnail", binding.pdfThumbnail, binding.progressBar
                )

                //set data
                binding.titleTv.text = title
                binding.authorTv.text = author
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    /*
    inner class HolderFavouriteBook(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail = binding.pdfThumbnail
        val progressBar = binding.progressBar
        val title = binding.titleTv
        val author = binding.authorTv
        val rating = binding.ratingBar
        val horizontalProgressBar = binding.progressBarHorizontal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFavouriteBook {
        binding = RowFavouriteBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderFavouriteBook(binding.root)
    }

    override fun onBindViewHolder(holder: HolderFavouriteBook, position: Int) {
        val model = bookArrayList[position]
        loadBookDetails(model, holder)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("bookId", model.id)
            context.startActivity(intent)
        }
    }

    private fun loadBookDetails(model: ModelPdf, holder: AdapterFavouriteBook.HolderFavouriteBook) {
        val bookId = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryId = "${snapshot.child("categoryId").value}"
                val author = "${snapshot.child("author").value}"
                val title = "${snapshot.child("title").value}"
                val url = "${snapshot.child("url").value}"
                val audio = "${snapshot.child("audio").value}"
                val uid = "${snapshot.child("uid").value}"
                val thumbnail = "${snapshot.child("image").value}"
                val description = "${snapshot.child("description").value}"

                //set favourite
                model.isFavourite = true
                model.title = title
                model.uid = uid
                model.url = url
                model.categoryId = categoryId
                model.author = author

                MyApplication.loadPdfThumbnail(
                    "$url", "$audio",
                    "$title", "$author", "$thumbnail", binding.pdfThumbnail, binding.progressBar
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
                binding.authorTv.text = author
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
     */

    override fun getItemCount(): Int {
        return bookArrayList.size
    }
}