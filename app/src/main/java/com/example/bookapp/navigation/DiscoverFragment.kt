package com.example.bookapp.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookapp.AdapterPdfUser
import com.example.bookapp.ModelPdf
import com.example.bookapp.databinding.FragmentDiscoverBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DiscoverFragment : Fragment {

    private lateinit var binding: FragmentDiscoverBinding

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoverBinding.inflate(LayoutInflater.from(context), container, false)
        binding.bookRv.setHasFixedSize(true);
        binding.bookRv2.setHasFixedSize(true);
        binding.bookRv3.setHasFixedSize(true);
        loadAllBooks()
        return binding.root
    }

    private fun loadAllBooks() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelPdf::class.java)
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                //set adapter to recyclerview
                binding.bookRv.adapter = adapterPdfUser
                binding.bookRv2.adapter = adapterPdfUser
                binding.bookRv3.adapter = adapterPdfUser
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}