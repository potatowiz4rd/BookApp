package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.bookapp.admin.DashboardAdminActivity
import com.example.bookapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //handle click go to register
        binding.noAccountTv.setOnClickListener() {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //handle login click
        binding.loginBtn.setOnClickListener {
            /*Steps
            * Input data
            * Validate Data
            * Login-Auth
            * Check user role*/
            validateData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        //input
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email...", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter your password...", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        //auth

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            //login success
            checkUserRole()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUserRole() {
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userRole = snapshot.child("role").value
                if (userRole == "user") {
                    startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                    finish()
                } else if (userRole == "admin") {
                    startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}