package com.charlesadebolaministries.kabodapp.ui.status

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.charlesadebolaministries.kabodapp.R
import com.charlesadebolaministries.kabodapp.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_status_list.*

class StatusListFragment : Fragment(), StatusItemClickListener {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var statusListAdapter = StatusListAdapter(arrayListOf())

    lateinit var userStatusElement: StatusListElement

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_list, container, false)
    }

    override fun onItemClicked(statusElement: StatusListElement) {
        startActivity(StatusListActivity.getIntent(context, statusElement))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userStatusLayout.setOnClickListener {
            onItemClicked(userStatusElement)
        }

        statusListAdapter.setOnItemClickListener(this)

        statusListRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = statusListAdapter
            addItemDecoration(DividerItemDecoration(this@StatusListFragment.context, DividerItemDecoration.VERTICAL))
        }
    }

    fun onVisible(){
        userStatus()
        statusListAdapter.onRefresh()
        refreshList()
    }

    fun refreshList(){
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener {doc ->
                if(doc.contains(DATA_USER_CHATS)){
                    val partners = doc[DATA_USER_CHATS]
                    for(partner in (partners as HashMap<String, String>).keys){
                        firebaseDB.collection(DATA_USERS)
                            .document(partner)
                            .get()
                            .addOnSuccessListener {documentSnapshot ->
                                val partner = documentSnapshot.toObject(User::class.java)
                                if(partner != null){
                                    if(!partner.status.isNullOrEmpty() && !partner.statusUrl.isNullOrEmpty()){
                                        val newElement = StatusListElement(partner.name, partner.imageUrl,
                                            partner.status, partner.statusUrl, partner.statusTime)
                                        statusListAdapter.addElement(newElement)
                                    }
                                }
                            }
                    }
                }
            }
    }

    fun userStatus(){
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener {documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if(user != null){
                    if(!user.status.isNullOrEmpty() && !user.statusUrl.isNullOrEmpty()){
                        val newElement = StatusListElement(user.name, user.imageUrl, user.status, user.statusUrl, user.statusTime)
                        userStatusElement = newElement
                        populateImage(userStatusIV.context, user.statusUrl, userStatusIV, R.drawable.profile_icon_small)
                        userNameTV.text = "You"
                        userTimeTV.text = user.statusTime

                    }
                }
            }
    }

}
