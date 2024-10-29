package com.example.chatter.auth.ui.viewmodel

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.MainActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.example.chatter.R
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


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

    fun googleSigin(context: ComponentActivity){
        isLoading.value = true
        val credentialManager  = CredentialManager.create(context)
        val googleOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()
        viewModelScope.launch{
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            singinWithCredintials(context , result.credential)
        }
    }
    private fun singinWithCredintials(context: ComponentActivity , credential: Credential){
        val googelCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val firebaseCredential = GoogleAuthProvider.getCredential(
            googelCredential.idToken,
            null
        )
        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
            .addOnSuccessListener{
                isLoading.value = false
                Toast.makeText(context , "User Created" , Toast.LENGTH_SHORT ).show()
                context.startActivity(Intent(context , MainActivity::class.java))
                context.finish()
            }
            .addOnFailureListener{
                Toast.makeText(context , it.message , Toast.LENGTH_SHORT ).show()
                isLoading.value = false
            }
    }
}