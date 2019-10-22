package com.charlesadebolaministries.kabodapp.conversation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlesadebolaministries.kabodapp.R
import com.charlesadebolaministries.kabodapp.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val conversationAdapter  = ConversationAdapter(arrayListOf(), userId)
    private var chatId: String? = null
    private var imageUrl: String? = null
    private var otherUserId: String? = null
    private var chatName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        chatId = intent.extras?.getString(PARAM_CHAT_ID)
        imageUrl = intent.extras?.getString(PARAM_IMAGE_URL)
        otherUserId = intent.extras?.getString(PARAM_OTHER_USER_ID)
        chatName = intent.extras?.getString(PARAM_CHAT_NAME)

        if(chatId.isNullOrEmpty() && userId.isNullOrEmpty()){
            Toast.makeText(this, "Chat room error", Toast.LENGTH_SHORT).show()
            finish()
        }

        topNameTV.text = chatName
        populateImage(this, imageUrl, topPhotoIV, R.drawable.profile_icon_small)

        messagesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }

        firebaseDB.collection(DATA_CHATS)
            .document(chatId!!)
            .collection(DATA_CHAT_MESSAGES)
            .orderBy(DATA_CHAT_MESSGAE_TIME)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    firebaseFirestoreException.printStackTrace()
                    return@addSnapshotListener
                }else{
                    if(querySnapshot != null){
                        for(change in querySnapshot.documentChanges){
                            when(change.type){
                                DocumentChange.Type.ADDED -> {
                                    val message = change.document.toObject(Message::class.java)
                                    if(message != null){
                                        conversationAdapter.addMessage(message)
                                        messagesRV.post {
                                            messagesRV.smoothScrollToPosition(conversationAdapter.itemCount - 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    companion object{

        private val PARAM_CHAT_ID = "Chat id"
        private val PARAM_IMAGE_URL = "Image url"
        private val PARAM_OTHER_USER_ID = "Other user id"
        private val PARAM_CHAT_NAME = "Chat name"

        fun newIntent(context: Context?, chatId: String?, imageUrl: String?, otherUserId: String?, chatName: String?) : Intent {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(PARAM_CHAT_ID, chatId)
            intent.putExtra(PARAM_IMAGE_URL, imageUrl)
            intent.putExtra(PARAM_OTHER_USER_ID, otherUserId)
            intent.putExtra(PARAM_CHAT_NAME, chatName)
            return intent
        }
    }

    fun onSend(view: View){

        if(!messageET.text.isNullOrEmpty()){
            val message = Message(userId, messageET.text.toString(), System.currentTimeMillis())
            firebaseDB.collection(DATA_CHATS)
                .document(chatId!!)
                .collection(DATA_CHAT_MESSAGES)
                .document()
                .set(message)

            messageET.setText("", TextView.BufferType.EDITABLE)
        }
    }

    fun onBack(view: View){

    }
}

