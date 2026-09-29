package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InAppMessage
import com.example.data.model.Member
import com.example.data.model.NotificationNotice
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.CurrentRole
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoticeAndMessagingScreen(
    notices: List<NotificationNotice>,
    messages: List<InAppMessage>,
    members: List<Member>,
    currentRole: CurrentRole,
    currentMemberId: Long,
    onBroadcastNoticeClick: () -> Unit,
    onSendMessage: (receiverId: Long, messageText: String, senderName: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var chatTargetMemberId by remember { mutableStateOf(if (currentRole == CurrentRole.ADMIN) 2L else 0L) }
    var messageInput by remember { mutableStateOf("") }

    val currentMember = members.find { it.id == currentMemberId }
    val senderName = if (currentRole == CurrentRole.ADMIN) "Manager (Admin)" else currentMember?.name ?: "Member"

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0 && currentRole == CurrentRole.ADMIN) {
                FloatingActionButton(
                    onClick = onBroadcastNoticeClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("broadcast_notice_fab")
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = "Broadcast Notice")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .testTag("notice_messaging_screen")
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("📢 Notices & Alerts (${notices.size})") },
                    modifier = Modifier.testTag("tab_notices")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("💬 In-App Messages") },
                    modifier = Modifier.testTag("tab_messages")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Notices Tab
                if (notices.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Outlined.Campaign,
                        title = "No Notices",
                        description = "Broadcast announcements or due reminders"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(notices, key = { it.id }) { n ->
                            NoticeCard(notice = n)
                        }
                    }
                }
            } else {
                // In-App Chat Tab
                Column(modifier = Modifier.fillMaxSize()) {
                    if (currentRole == CurrentRole.ADMIN) {
                        // Admin chooses which member to chat with
                        var memberDropdownExpanded by remember { mutableStateOf(false) }
                        val activeChatMember = members.find { it.id == chatTargetMemberId }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Chat with: ", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalButton(onClick = { memberDropdownExpanded = true }) {
                                Text(activeChatMember?.name ?: "Select Member")
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = memberDropdownExpanded,
                                onDismissRequest = { memberDropdownExpanded = false }
                            ) {
                                members.filter { it.role != "ADMIN" }.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text("${m.name} (Room ${m.roomNumber})") },
                                        onClick = {
                                            chatTargetMemberId = m.id
                                            memberDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Filter messages between current user and target
                    val filteredMessages = messages.filter { msg ->
                        if (currentRole == CurrentRole.ADMIN) {
                            (msg.senderId == 0L && msg.receiverId == chatTargetMemberId) ||
                                    (msg.senderId == chatTargetMemberId && msg.receiverId == 0L)
                        } else {
                            (msg.senderId == currentMemberId && msg.receiverId == 0L) ||
                                    (msg.senderId == 0L && msg.receiverId == currentMemberId)
                        }
                    }

                    // Message list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (filteredMessages.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Outlined.Chat,
                                    title = "No Messages Yet",
                                    description = "Send a message regarding dues, meal updates, or mess queries"
                                )
                            }
                        } else {
                            items(filteredMessages, key = { it.id }) { msg ->
                                val isMe = if (currentRole == CurrentRole.ADMIN) msg.senderId == 0L else msg.senderId == currentMemberId
                                ChatBubble(message = msg, isMe = isMe)
                            }
                        }
                    }

                    // Input bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Type message...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input"),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    val receiver = if (currentRole == CurrentRole.ADMIN) chatTargetMemberId else 0L
                                    onSendMessage(receiver, messageInput, senderName)
                                    messageInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("send_chat_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeCard(notice: NotificationNotice) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(notice.timestamp))
    val (icon, bg, color) = when (notice.type) {
        "DUE_REMINDER" -> Triple(Icons.Default.Warning, DueRedContainer, DueRed)
        "MEAL_UPDATE" -> Triple(Icons.Default.Restaurant, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
        "ANNOUNCEMENT" -> Triple(Icons.Default.Campaign, AmberTertiaryContainer, AmberTertiary)
        else -> Triple(Icons.Default.Info, InfoBlueContainer, InfoBlue)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notice.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notice.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: InAppMessage, isMe: Boolean) {
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val dateStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (!isMe) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
