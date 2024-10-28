package com.example.chatter.auth.ui.viewmodel

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatter.MainActivity
import com.example.chatter.auth.ui.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class SigningSignOutViewModel @Inject constructor() : ViewModel(){

    var isLoading  = MutableStateFlow(false)
        private set

    fun Singing(context:ComponentActivity, email: String, password : String){
        isLoading.value = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email , password)
            .addOnSuccessListener({result->
                isLoading.value = false
                Toast.makeText(context , "User login" , Toast.LENGTH_SHORT ).show()
                context.startActivity(Intent(context , MainActivity::class.java))
                context.finish()
            })
            .addOnFailureListener({error->
                isLoading.value = false
                Toast.makeText(context , error.message , Toast.LENGTH_SHORT ).show()
            })
    }
    fun SignUp(context:ComponentActivity, email: String , password: String){
        isLoading.value = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email , password)
            .addOnSuccessListener{
                isLoading.value = false
                Toast.makeText(context , "User Created" , Toast.LENGTH_SHORT ).show()
                context.startActivity(Intent(context , MainActivity::class.java))
                context.finish()
            }
            .addOnFailureListener{
                isLoading.value = false
                Toast.makeText(context , it.message , Toast.LENGTH_SHORT ).show()
            }
    }
}