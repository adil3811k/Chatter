package com.example.chatter.auth.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chatter.R
import com.example.chatter.auth.ui.AuthActivity
import com.example.chatter.auth.ui.Singup
import com.example.chatter.auth.ui.viewmodel.SigningSignOutViewModel

@Composable
fun SignScreen(
    isLoading: Boolean,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    onSigIn: (String, String) -> Unit,
) {
    var email by remember {
        mutableStateOf(value = "")
    }
    var password by remember {
        mutableStateOf(value = "")
    }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            contentAlignment = Alignment.Center
        ){
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier.size(200.dp),
                tint =Color.Unspecified
            )
        }
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            maxLines = 1,
            label = {Text("Enter Email")},
            modifier = modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier =  modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            maxLines = 1,
            label = {Text("Enter password")},
            modifier = modifier.fillMaxWidth(0.8f)
        )
        TextButton({
            navHostController.navigate(Singup)
        }) {
            Text("if you have not account register hare")
        }
        Button({
            onSigIn(email, password)
        },
            modifier = modifier.fillMaxWidth(0.8f),
            enabled = email.isNotBlank() && password.isNotBlank() && email.contains("@") && password.length>5
        ) {
            Text("Sing in")
        }
        if (isLoading){
            CircularProgressIndicator()
        }
    }
}
