package com.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bookapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog

        //handle back button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.registerBtn.setOnClickListener {
            validatedata()
        }

    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validatedata() {

        //input
        name = binding.nameEdt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.passwordConfirmEt.text.toString().trim()

        //validate
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email patterns
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show()
        } else if (cPassword.isEmpty()) {
            Toast.makeText(this, "Confirm password...", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        //firebase auth

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            //add account to db
            updateUserInfo()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfo() {
        val timestamp = System.currentTimeMillis()

        // get current user uid
        val uid = firebaseAuth.uid

        //setup data to add to db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImg"] = ""
        hashMap["role"] = "user"
        hashMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!).setValue(hashMap).addOnSuccessListener {
            //open user dashboard
            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}