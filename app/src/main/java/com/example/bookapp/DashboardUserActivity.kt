package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bookapp.databinding.ActivityDashBoardUserBinding
import com.example.bookapp.navigation.DiscoverFragment
import com.example.bookapp.navigation.LibraryFragment
import com.example.bookapp.navigation.MenuFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardUserBinding

    //private lateinit var firebaseAuth: FirebaseAuth

    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(LibraryFragment())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navLibrary -> {
                    loadFragment(LibraryFragment())
                    true
                }
                R.id.navDiscovery -> {
                    loadFragment(DiscoverFragment())
                    true
                }
                R.id.navMenu -> {
                    loadFragment(MenuFragment())
                    true
                }
                else -> {
                    throw IllegalStateException("Fragment is not correct")
                }
            }
        }
        /**
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle logout
        binding.logoutBtn.setOnClickListener {
        firebaseAuth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        }
         */
    }

    /**
    private fun checkUser() {
    val firebaseUser = firebaseAuth.currentUser
    if (firebaseUser == null) {
    //not logged
    } else {
    //logged in
    val email = firebaseUser.email
    }
    }
     */

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

}