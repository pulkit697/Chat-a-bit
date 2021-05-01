package com.example.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.adapters.ChatAdapter
import com.example.chatapp.models.*
import com.example.chatapp.util.KEY_AUTH_UID
import com.example.chatapp.util.KEY_IMAGE
import com.example.chatapp.util.KEY_USER_NAME
import com.example.chatapp.util.isSameDayAs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider
import kotlinx.android.synthetic.main.activity_chat_box.*

class ChatActivity : AppCompatActivity() {

    private val friendName:String by lazy{
        intent.getStringExtra(KEY_USER_NAME)!!
    }

    private val friendId:String by lazy{
        intent.getStringExtra(KEY_AUTH_UID)!!
    }

    private val friendImage:String by lazy{
        intent.getStringExtra(KEY_IMAGE)!!
    }

    private val mAuthUid:String by lazy{
        FirebaseAuth.getInstance().uid!!
    }

    // db is real time database instance
    private val db:DatabaseReference by lazy{
        FirebaseDatabase.getInstance().reference
    }

    private lateinit var currentUser:User
    private var messagesList = mutableListOf<ChatEvent>()

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(IosEmojiProvider())
        setContentView(R.layout.activity_chat_box)

        FirebaseFirestore.getInstance().collection("users").document(mAuthUid).get()
            .addOnSuccessListener {
                currentUser = it.toObject(User::class.java)!!
            }

        chatAdapter = ChatAdapter(messagesList,mAuthUid)
        rvChats.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }

        listenToMessages()

        tvFriendNameChatActivity.text = friendName
        Picasso.get().load(friendImage).error(R.drawable.default_avatar).into(ivFriendImageChatActivity)

        ivSendMessageButton.setOnClickListener {
            eetMessageChatActivity.text.let {
                if (!it.isNullOrEmpty()){
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }

    }

    private fun sendMessage(msg:String){
        val msgUniqueId = getMessages(friendId).push().key
        checkNotNull(msgUniqueId){ "Should not be null "}
        val mMsg = Message(msg,msgUniqueId,mAuthUid)
        getMessages(friendId).child(msgUniqueId).setValue(mMsg).addOnSuccessListener {
            /* TO BE IMPLEMENTED */
        }
        updateLastMessage(mMsg)
    }

    private fun markAsRead(){
        getInbox(friendId,mAuthUid).child("count").setValue(0)
    }

    private fun listenToMessages()
    {
        getMessages(friendId)
                .orderByKey()
                .addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        addMessage(snapshot.getValue(Message::class.java)!!)
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        /* Do Nothing */
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        /* Do Nothing */
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        /* Do Nothing */
                    }

                    override fun onCancelled(error: DatabaseError) {
                        /* Do Nothing */
                    }
                })
    }

    private fun addMessage(msgMap: Message) {
        val eventBefore = messagesList.lastOrNull()
        if(eventBefore==null || !eventBefore.sentAt.isSameDayAs(msgMap.sentAt)){
            messagesList.add(DateHeader(msgMap.sentAt,this))
        }
        messagesList.add(msgMap)
        chatAdapter.notifyItemInserted(messagesList.size-1)
        rvChats.scrollToPosition(messagesList.size-1)
    }

    private fun updateLastMessage(msgMap: Message) {
        val inboxMap = Inbox(msgMap.msg,friendId,friendName,friendImage,count = 0)
        getInbox(mAuthUid,friendId).setValue(inboxMap).addOnSuccessListener {
            getInbox(friendId,mAuthUid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)
                    inboxMap.apply {
                        from = msgMap.senderId
                        name = currentUser.name
                        image = currentUser.imageUrl
                        count = 1
                    }
                    value?.let {
                        if(it.from == msgMap.senderId){
                            inboxMap.count = inboxMap.count+1
                        }
                    }
                    getInbox(friendId,mAuthUid).setValue(inboxMap)
                }

                override fun onCancelled(error: DatabaseError) {
                    /* Do Nothing */
                }
            })
        }
    }

    private fun getMessages(friendId: String) = db.child("messages/${getInterChatId(friendId)}")

    private fun getInbox(toUser: String, fromUser:String) = db.child("chats/$toUser/$fromUser")

    private fun getInterChatId(friendId:String):String{
        if(friendId<mAuthUid)
            return friendId+mAuthUid
        return mAuthUid+friendId
    }

}