package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookapp.databinding.ActivityDashBoardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardUserBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            //not logged
            binding.subTitleTv.text = "Not Logged in"
        } else {
            //logged in
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}