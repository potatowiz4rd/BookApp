package com.example.bookapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookapp.adapter.AdapterFavouriteBook
import com.example.bookapp.databinding.FragmentDiscoverBinding
import com.example.bookapp.databinding.FragmentLibraryFavouriteBinding
import com.example.bookapp.model.ModelPdf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LibraryFavouriteFragment : Fragment() {

    private lateinit var binding: FragmentLibraryFavouriteBinding
    private lateinit var booksArrayList: ArrayList<ModelPdf>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var adapterFavouriteBook: AdapterFavouriteBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentLibraryFavouriteBinding.inflate(LayoutInflater.from(context), container, false)
        loadFavouriteBooks()
        return binding.root
    }

    private fun loadFavouriteBooks() {
        booksArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favourites")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    booksArrayList.clear()
                    for (ds in snapshot.children) {
                        val bookId = "${ds.child("bookId").value}"

                        val modelPdf = ModelPdf()
                        modelPdf.id = bookId

                        booksArrayList.add(modelPdf)
                    }
                    adapterFavouriteBook = AdapterFavouriteBook(context!!, booksArrayList)
                    binding.favRv.adapter = adapterFavouriteBook
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}