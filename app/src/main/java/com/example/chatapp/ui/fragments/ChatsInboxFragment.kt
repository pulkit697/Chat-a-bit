package com.example.chatapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.models.Inbox
import com.example.chatapp.models.InboxChatViewHolder
import com.example.chatapp.ui.ChatActivity
import com.example.chatapp.util.KEY_AUTH_UID
import com.example.chatapp.util.KEY_IMAGE
import com.example.chatapp.util.KEY_USER_NAME
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.fragment_chats_inbox.*

class ChatsInboxFragment : Fragment(R.layout.fragment_chats_inbox) {

    lateinit var mAdapter: FirebaseRecyclerAdapter<Inbox,InboxChatViewHolder>

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpAdapter()
        return inflater.inflate(R.layout.fragment_chats_inbox,container,false)
    }

    private fun setUpAdapter()
    {
        val query:Query = db.reference.child("chats").child(auth.uid!!)
        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(query,Inbox::class.java).build()

        mAdapter = object : FirebaseRecyclerAdapter<Inbox,InboxChatViewHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ) = InboxChatViewHolder(layoutInflater.inflate(R.layout.layout_rv_item,parent,false))

            override fun onBindViewHolder(
                holder: InboxChatViewHolder,
                position: Int,
                model: Inbox
            ) {
                holder.bind(model) { name: String, image: String, uid: String ->
                    val intent = Intent(requireContext(),ChatActivity::class.java)
                    intent.putExtra(KEY_USER_NAME,name)
                    intent.putExtra(KEY_IMAGE,image)
                    intent.putExtra(KEY_AUTH_UID,uid)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvChatsInbox.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
