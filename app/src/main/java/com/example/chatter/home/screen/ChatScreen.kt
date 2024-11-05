package com.example.chatter.home.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatter.home.viewModel.ChatViewmodel
import com.example.chatter.modul.Message
import com.example.chatter.ui.theme.ChatterTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ChatScreenCom(
    uid: String,
    modifier: Modifier = Modifier
) {
    val viewModel  = hiltViewModel<ChatViewmodel>()
    val state  = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.setListner(uid)
        Log.d("Firebase" ,"Listener attach")
    }
    Column(
        modifier = modifier.fillMaxSize()
    ){
        LazyColumn(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 5.dp, top = 5.dp),
            reverseLayout = true
        ){
            items(state.value){message->
                MessageBubble(message)
            }
        }
        EnterMessageCom(
            modifier =  modifier.fillMaxWidth()
        ) {message->
            viewModel.sendMessage(message , uid)
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val isCurrentUser = FirebaseAuth.getInstance().uid == message.sandBy
    Log.d("Firebase", message.sandBy.toString())
    val Color = if (isCurrentUser) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val TextColor = if (isCurrentUser) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (isCurrentUser) Alignment.BottomEnd else Alignment.BottomStart
    ){
        Row (
            modifier = modifier
                .clip(RoundedCornerShape(20))
                .background(Color),
        ){
            Text(
                text = message.text ?: "Null",
                fontSize = 14.sp,
                color = TextColor,
                modifier = modifier
                    .padding(start =  8.dp , top = 8.dp, bottom = 8.dp, end = 8.dp)
            )
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
// this is composable for enter massage and send
@Composable
 fun EnterMessageCom(
    modifier: Modifier = Modifier,
    onSend:(String)-> Unit
) {
    var message by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .padding(start = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        OutlinedTextField(
            value =  message,
            onValueChange = {message = it},
            label = {Text("Enter Message")},
            colors = OutlinedTextFieldDefaults.colors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer ,
                unfocusedContainerColor =  MaterialTheme.colorScheme.primaryContainer,
            ),
            singleLine = true,
            shape = RoundedCornerShape(30),
            trailingIcon = {
                IconButton({
                    onSend(message)
                    message = ""

                }) {
                    Icon(Icons.Default.Send , "Send Icon")
                }
            },
            modifier = modifier.fillMaxWidth()
        )
    }
}