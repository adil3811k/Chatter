@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chatter.group.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.chatter.GroupChats
import com.example.chatter.GroupsList
import com.example.chatter.group.viewModel.GroupViewModel
import com.example.chatter.home.screen.EnterMessageCom
import com.example.chatter.modul.Group
import com.example.chatter.modul.GroupsChat
import com.example.chatter.modul.Message
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCom(modifier : Modifier = Modifier) {
    val viewmodel : GroupViewModel= hiltViewModel()
    val navController = rememberNavController()
    NavHost(navController , GroupsList){
        composable<GroupsList>{
            Groups(viewmodel){
                navController.navigate(GroupChats(it))
            }
        }
        composable<GroupChats>{
            val groupChats =it.toRoute<GroupChats>()
            GroupsChat(groupChats.id,viewmodel)
        }
    }
}

@Composable
private fun GroupsChat(
    id: String,
    viewmodel: GroupViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewmodel.setChatListener(id)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Group")},
                actions = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton({
                            viewmodel.copyID(context , id)
                        }) {
                            Icon(Icons.Default.ContentCopy, "Copy Icon")
                        }
                        Text("Copy group id", modifier = modifier.padding(top = 12.dp))
                    }
                }
            )
        }
    ) {paddinvalue->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddinvalue)
        ){
            LazyColumn(
                modifier = modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 5.dp, top = 5.dp),
                reverseLayout = true
            ){
                items(state.groupchats){groupsChat->
                    GroupMessageBubble(groupsChat)
                }
            }
            EnterMessageCom(
                modifier =  modifier.fillMaxWidth()
            ) {message->
                viewmodel.sendMessage(message ,id)
            }
        }
    }
}


@Composable
private fun Groups(
    groupViewModel: GroupViewModel,
    modifier: Modifier = Modifier,
    onitemCLick:(String)-> Unit
) {
    val state by groupViewModel.state.collectAsStateWithLifecycle()
    var isDialogOpen by remember { mutableStateOf(false) }
    Scaffold (
        floatingActionButton = {
            FloatingActionButton({isDialogOpen = true}) {
                Icon(Icons.Default.Add, "Add Icons")
            }
        },
        topBar = {
            TopAppBar(
                title = {Text("Group")},
            )
        }
    ){ paddinvalue->
        LazyColumn(modifier = modifier.padding(paddinvalue)) {
            items(state.list){group->
                GroupItem(group){
                    onitemCLick(it)
                }
            }
        }
    }
    AnimatedVisibility (isDialogOpen){
        GroupDialog(
            onDismiss = {isDialogOpen= false},
            onCreate = { name, description->
                groupViewModel.createGroup(name , description)
                isDialogOpen = false
            },
            onJoin = {groupId->
                groupViewModel.joinGroup(groupId)
                isDialogOpen = false
            },
        )
    }
}

@Composable
private fun GroupItem(
    group: Group,
    modifier: Modifier = Modifier,
    onCardClick:(String)-> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable{onCardClick(group.groupId?:"")}
    ) {
        val paddgingvalue = PaddingValues(top = 12.dp , start = 12.dp , end = 12.dp)
        Text(
            text = "Group name : ${group.title}",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            modifier = modifier.padding(paddgingvalue)
        )
        Spacer(modifier = modifier.height(20.dp))
        Text(
            text = group.description?:"",
            fontWeight = FontWeight(300),
            fontSize = 24.sp,
            modifier = modifier.padding(paddgingvalue).padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun GroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String , String) -> Unit,
    onJoin:(String)-> Unit,
) {
    val IsJoining = remember { mutableStateOf(true) }
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val groupID = remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton({
                if (IsJoining.value){
                    onJoin(groupID.value)
                }else{
                    onCreate(name.value , description.value)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text("Cancel")
            }
        },
        title = {Text(if(IsJoining.value)"Join ths group" else "Create the group")},
        text ={
            Column {
                Row{
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Join the group")
                        RadioButton(
                            selected = IsJoining.value,
                            onClick = {IsJoining.value = true}
                        )
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Create the group")
                        RadioButton(
                            selected = !IsJoining.value,
                            onClick = {IsJoining.value = false}
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                if (IsJoining.value){
                    OutlinedTextField(
                        value = groupID.value,
                        onValueChange = {groupID.value = it},
                        label = {Text("Enter the Group Id")}
                    )
                }else{
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {name.value = it},
                        label = {Text("Enter the group name")}
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = description.value,
                        onValueChange = {description.value = it},
                        label = {Text("Enter the group Description")}
                    )
                }
            }
        }
    )
}


@Composable
fun GroupMessageBubble(
    message: GroupsChat,
    modifier: Modifier = Modifier
) {
    val isCurrentUser = FirebaseAuth.getInstance().uid == message.senderID
    Log.d("Firebase", message.senderID.toString())
    val Color = if (isCurrentUser) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val TextColor = if (isCurrentUser) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (isCurrentUser) Alignment.BottomEnd else Alignment.BottomStart
    ){
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(20))
                .background(Color),
        ) {
            Text(
                text =  message.senderName?:"",
                fontSize = 8.sp,
                color = TextColor,
                modifier = modifier
                    .padding(start =  8.dp , top = 8.dp, end = 8.dp)
            )
            Row{
                SelectionContainer(
                    modifier = modifier
                        .padding(start =  8.dp, bottom = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = message.text ?: "Null",
                        fontSize = 14.sp,
                        color = TextColor,

                    )
                }
                Text(
                    text = sdf.format(message.createdAt.toDate()),
                    fontSize = 10.sp,
                    color = TextColor,
                    modifier = modifier
                        .padding(end =  5.dp, top = 8.dp , bottom = 8.dp)
                )
            }
        }
    }
}