package com.example.bookapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookapp.LibraryAllFragment
import com.example.bookapp.LibraryDownloadedFragment
import com.example.bookapp.LibraryFavouriteFragment

class FragmentPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            LibraryAllFragment()
        } else if (position == 1) {
            LibraryFavouriteFragment()
        } else {
            LibraryDownloadedFragment()
        }
    }

}