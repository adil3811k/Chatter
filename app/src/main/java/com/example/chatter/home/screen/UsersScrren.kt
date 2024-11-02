package com.example.chatter.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.chatter.ChatScreen
import com.example.chatter.home.viewModel.UsersViewModel
import com.example.chatter.modul.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val viewmodel:UsersViewModel = hiltViewModel()
    val state = viewmodel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text(text ="Chatter app")},
            )
        }
    ) { innerpadding->
        LazyColumn (modifier =modifier.padding(innerpadding)){
            items(state.value){
                UserCard(it, modifier.padding(10.dp) ){uid->
                   onClick(uid)
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    modifier: Modifier = Modifier,
    onClick:(String)-> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable{onClick(user.uid?:"UID")},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =Arrangement.SpaceAround,
    ) {
        Box(
            modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
                .size(70.dp)
                .clip(RoundedCornerShape(50))
                .border(2.dp , MaterialTheme.colorScheme.onTertiaryContainer, RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
        ){
            Text(
                text = user.name?.get(0).toString(),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 25.sp,
                modifier = modifier
                    .align(Alignment.Center)
            )
        }
        Text(
            text= user.name?:"Display name",
            fontSize = 15.sp,
            modifier = modifier.weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp)
        )
    }
}