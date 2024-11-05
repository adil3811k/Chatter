package com.example.chatter.group.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.modul.Group
import com.example.chatter.modul.GroupsChat
import com.example.chatter.modul.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class GroupUiState(
    val list: List<Group> = emptyList<Group>(),
    val groupchats: List<GroupsChat> = emptyList<GroupsChat>()
)

class GroupViewModel: ViewModel() {

    private val _state = MutableStateFlow(GroupUiState())
    val state = _state
        .onStart {
            attachLListener()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            GroupUiState()
        )


    private fun attachLListener(){
        val uid = FirebaseAuth.getInstance().uid?:""
        Firebase.firestore.collection("Users")
            .document(uid)
            .addSnapshotListener{snapshot, error->
                if (error!=null){
                    Log.d("Firebase", error.message?:"Error is null")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        viewModelScope.launch{
                            getGroupFormString(user.group)
                        }
                    }
                }
            }
    }

    private suspend fun   getGroupFormString(list: List<String>){
        val newGroup = mutableListOf<Group>()
            list.forEach {
                val group = Firebase.firestore.collection("Group")
                    .document(it).get().await().toObject(Group::class.java)
                group?.let { element -> newGroup.add(element) }
            }
        _state.update {
            it.copy(
                list = newGroup
            )
        }
        }
    fun sendMessage(text: String, id: String){
        viewModelScope.launch{
            val currentUser = FirebaseAuth.getInstance().uid.getUserByUid()
            val message = GroupsChat(currentUser?.name , currentUser?.uid , text , Timestamp.now())
            Firebase.firestore.collection("Group")
                .document(id)
                .collection("Chat")
                .document()
                .set(message)
        }
    }

    fun setChatListener(id: String){
        Firebase.firestore.collection("Group")
            .document(id)
            .collection("Chat")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener{snapshot , error->
                if (error!=null){
                    Log.d("Firebase", error.message?:"Error")
                    return@addSnapshotListener
                }
                if (snapshot!=null){
                    val list = snapshot.toObjects(GroupsChat::class.java)
                    _state.update {
                        it.copy(
                            groupchats = list
                        )
                    }
                }
            }
    }

    fun joinGroup(groupId: String){
        viewModelScope.launch{
            val currentUser = FirebaseAuth.getInstance().uid.getUserByUid()
            val list: MutableList<String> = currentUser?.group?:mutableListOf()
            list.add(groupId)
            Firebase.firestore.collection("Users")
                .document(currentUser?.uid?:"")
                .set(currentUser?:User())
        }
    }

    fun createGroup(Tital: String , Discription: String){
        val id = Firebase.firestore.collection("Group").document().id
        val group = Group(
            title =  Tital,
            description = Discription,
            groupId = id,
            owner = FirebaseAuth.getInstance().uid,
            createdAt = Timestamp.now()
        )
        Firebase.firestore.collection("Group")
            .document(id)
            .set(group)
        joinGroup(id)
    }
    private suspend fun String?.getUserByUid(): User?{
        if (this!=null){
            val user =  Firebase.firestore.collection("Users")
                .document(this)
                .get().await()
                .toObject(User::class.java)
            return user
        }else{
            return null
        }
    }
    fun copyID(context:Context, text: String){
        try {
            val clipboardManager = context.getSystemService(ClipboardManager::class.java)
            val clipdata = ClipData.newPlainText("id of group", text)
            clipboardManager.setPrimaryClip(clipdata)
            clipboardManager.setPrimaryClip(clipdata)
            Toast.makeText(context , "Group id copy success full", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(context , "Group id not copy ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}