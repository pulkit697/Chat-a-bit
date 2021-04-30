package com.example.chatapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.EmptyViewHolder
import com.example.chatapp.models.User
import com.example.chatapp.models.UserViewHolder
import com.example.chatapp.ui.ChatActivity
import com.example.chatapp.util.*
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_people_list.*
import java.lang.Exception

class PeopleListFragment : Fragment() {

    lateinit var mAdapter:FirestorePagingAdapter<User,RecyclerView.ViewHolder>

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance().collection("users").orderBy("name",Query.Direction.ASCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpAdapter()
        return inflater.inflate(R.layout.fragment_people_list,container,false)
    }

    private fun setUpAdapter() {
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(2)
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .build()
        val options = FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(db,config,User::class.java)
                .build()
        mAdapter = object : FirestorePagingAdapter<User,RecyclerView.ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                    when(viewType){
                        EMPTY_VIEW_TYPE -> EmptyViewHolder(layoutInflater.inflate(R.layout.layout_empty,parent,false))
                        else -> UserViewHolder(layoutInflater.inflate(R.layout.layout_rv_item,parent,false))
                    }


            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
                if(holder is UserViewHolder)
                    holder.bind(user = model){ name: String, image: String, uid: String ->
                        val intent = Intent(requireContext(),ChatActivity::class.java)
                        intent.putExtra(KEY_USER_NAME,name)
                        intent.putExtra(KEY_IMAGE,image)
                        intent.putExtra(KEY_AUTH_UID,uid)
                        startActivity(intent)
                    }
            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return if (auth.uid == item?.uid)
                            EMPTY_VIEW_TYPE
                        else
                            USER_NORMAL_VIEW_TYPE
            }

            override fun onError(e: Exception) {
                super.onError(e)
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                    }
                    LoadingState.LOADING_MORE -> {
                    }
                    LoadingState.LOADED -> {
                    }
                    LoadingState.FINISHED -> {
                    }
                    LoadingState.ERROR -> {
                    }
                }
            }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPeopleList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

}
