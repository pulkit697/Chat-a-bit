package com.example.chatapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.ui.fragments.PeopleListFragment
import com.example.chatapp.ui.fragments.ChatsInboxFragment

class ScreenSlideAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment  =  when(position){
        0 -> ChatsInboxFragment()
        else -> PeopleListFragment()
    }
}
