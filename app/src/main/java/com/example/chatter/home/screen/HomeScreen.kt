package com.example.chatter.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.chatter.ChatScreen
import com.example.chatter.HomeScreen

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, HomeScreen){
        composable<HomeScreen>{
            UsersScreen{
                navController.navigate(ChatScreen(it))
            }
        }
        composable<ChatScreen>{
            val chatScreen = it.toRoute<ChatScreen>()
            ChatScreenCom(chatScreen.id)
        }
    }
}

