package com.example.bookapp.navigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.bookapp.R
import com.example.bookapp.SearchBookActivity
import com.example.bookapp.adapter.AdapterPdfUser
import com.example.bookapp.model.ModelPdf
import com.example.bookapp.databinding.FragmentDiscoverBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class DiscoverFragment : Fragment {

    private lateinit var binding: FragmentDiscoverBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        uid = auth.currentUser?.uid ?: ""

        // Query the database to retrieve the user's name
        val userRef = database.reference.child("Users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value?.toString() ?: ""
                val profileImg = "${snapshot.child("profileImg").value}"
                binding.greetingTv.text = "Hello, $name"

                try {
                    Picasso.get().load(profileImg).into(binding.profileIv)
                } catch (e: Exception) {
                    //check null
                    binding.profileIv.setImageResource(R.drawable.circle_user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoverBinding.inflate(LayoutInflater.from(context), container, false)
        ///Picasso.get().load(R.drawable.banner_crop).fit().into(binding.bannerIv)

        val slideModels = ArrayList<SlideModel>()
        slideModels.add(SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP))
        slideModels.add(SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP))
        slideModels.add(SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP))
        binding.imageSlider.setImageList(slideModels)

        binding.bookRv.setHasFixedSize(true);
        binding.bookRv2.setHasFixedSize(true);
        binding.searchView.setOnClickListener() {
            startActivity(Intent(context, SearchBookActivity::class.java))
        }
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
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}