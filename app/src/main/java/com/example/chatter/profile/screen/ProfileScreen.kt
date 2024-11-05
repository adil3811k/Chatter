package com.example.chatter.profile.screen

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatter.modul.User
import com.example.chatter.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profilecom(modifier: Modifier = Modifier) {
    val viewmodel = hiltViewModel<ProfileViewModel>()
    val user = viewmodel.user.collectAsStateWithLifecycle()
    val context = LocalContext.current as ComponentActivity
    val isDialogOpen = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Profile")},
                actions = {
                    IconButton({viewmodel.logout(context)}) {
                        Icon(Icons.Default.Logout, "Logout icon")
                    }
                }
            )
        }
    ) {paddinValue->
        UserCom(user.value, Modifier.padding(paddinValue)){
            isDialogOpen.value = true
        }
    }
    AnimatedVisibility(isDialogOpen.value) {
        ProfileDialog(
            user.value ,
            { isDialogOpen.value = false }
            ,{ isDialogOpen.value = false
            viewmodel.changeName(it)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDialog(
    user: User,
    onDismiss:()-> Unit,
    onOK:(String)-> Unit,
) {
    var name   = remember { mutableStateOf(user.name?:"") }
    AlertDialog(
        onDismissRequest =  { onDismiss() },
        confirmButton = {TextButton({onOK(name.value)}) {
            Text(text =  "OK")
        }},
        dismissButton = {
            TextButton({onDismiss()}) {
                Text(text =  "Cancel")
            }
        },
        title = {Text("Change the user name")},
        text = {
            OutlinedTextField(
                value =  name.value,
                onValueChange = {name.value = it}
            )
        }
    )
}

@Composable
private fun UserCom(
    user: User,
    modifier : Modifier = Modifier,
    dialogOpen:()-> Unit
) {
    val groups = if (user.group!=null) user.group.joinToString(" ") else "User have not join any group"
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(50))
                .border(2.dp, MaterialTheme.colorScheme.onTertiaryContainer, RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSecondaryContainer),
        ){
            Text(
                text = user.name?.get(0).toString(),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 25.sp,
                modifier = modifier
                    .align(Alignment.Center)
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(user.name?:"Display name", fontSize = 32.sp)
            IconButton({
                dialogOpen()
            }) {
                Icon(Icons.Default.Edit,"Edit Icon")
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(text  = "Email : ${user.email}", fontSize = 22.sp)
        Spacer(Modifier.height(12.dp))
        Text(text  = "UID : ${user.uid}", fontSize = 22.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
        Spacer(Modifier.height(12.dp))
        Text(text  = "Groups : $groups", fontSize = 22.sp)
    }
}
