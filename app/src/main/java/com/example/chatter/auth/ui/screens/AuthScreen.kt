package com.example.chatter.auth.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatter.SigIn
import com.example.chatter.Singup
import com.example.chatter.auth.ui.viewmodel.SigningSignOutViewModel

@Composable
fun AuthScreen() {
    val navController = rememberNavController()
    val viewmodel = hiltViewModel<SigningSignOutViewModel>()
    val isLoading = viewmodel.isLoading.collectAsState()
    val context= LocalContext.current as ComponentActivity
    NavHost(navController =  navController , startDestination = SigIn){
        composable<SigIn>{
            SignScreen(
                isLoading = isLoading.value,
                navHostController =  navController,
                onSigIn = {email , password -> viewmodel.Singing(context, email , password)},
                googleSignIn = {viewmodel.googleSigin(it)}
            )
        }
        composable<Singup>{
            SignUpScreen(
                isLoading = isLoading.value ,
                onSigUp = {email  , password-> viewmodel.SignUp( context, email , password)}
            )
        }
    }

}