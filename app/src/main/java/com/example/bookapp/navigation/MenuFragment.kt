package com.example.bookapp.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookapp.MainActivity
import com.example.bookapp.databinding.FragmentMenuBinding
import com.google.firebase.auth.FirebaseAuth


class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            //not logged
        } else {
            //logged in
            val email = firebaseUser.email
        }
    }
}