package com.example.wedding_planner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wedding_planner.data.model.Guest
import com.example.wedding_planner.data.model.enums.SpecialRole
import com.example.wedding_planner.ui.theme.GoldBackground
import com.example.wedding_planner.ui.theme.GoldColor

@Composable
fun GuestItemCard(
    guest: Guest,
    onClick: () -> Unit, 
    onUpdateRole: (SpecialRole) -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val isHonorGuest = guest.isHonorGuest()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)), 
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isHonorGuest) 2.dp else 0.dp),
        border = if (isHonorGuest) {
            BorderStroke(1.dp, GoldColor.copy(alpha = 0.5f))
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GuestAvatar(name = guest.name, isHonorGuest = isHonorGuest)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guest.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val phoneInfo = if (guest.phone.isNotBlank()) " • ${guest.phone}" else ""
                Text(
                    text = "${guest.side.label} • ${guest.kinship.label}$phoneInfo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isHonorGuest && guest.specialRole != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    HonorBadge(roleLabel = guest.specialRole.label)
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opções",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "Definir Função",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    SpecialRole.entries.filter { it != SpecialRole.NONE }.forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    role.label,
                                    fontWeight = if (guest.specialRole == role) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            trailingIcon = if (guest.specialRole == role) {
                                { Icon(Icons.Default.Star, null, tint = GoldColor) }
                            } else null,
                            onClick = {
                                onUpdateRole(role)
                                showMenu = false
                            }
                        )
                    }
                    if (isHonorGuest) {
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Remover Função Especial") },
                            leadingIcon = { Icon(Icons.Outlined.Person, null) },
                            onClick = {
                                onUpdateRole(SpecialRole.NONE)
                                showMenu = false
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Excluir Convidado") },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                        },
                        onClick = {
                            onDelete()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GuestAvatar(name: String, isHonorGuest: Boolean) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"

    val bgColor = if (isHonorGuest) GoldBackground else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (isHonorGuest) GoldColor else MaterialTheme.colorScheme.onSecondaryContainer
    val borderColor = if (isHonorGuest) GoldColor else Color.Transparent
    Box {
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(46.dp),
            color = bgColor,
            border = BorderStroke(if (isHonorGuest) 2.dp else 0.dp, borderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
        if (isHonorGuest) {
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = null,
                tint = GoldColor,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .background(Color.White, CircleShape)
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun HonorBadge(roleLabel: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = GoldColor.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, GoldColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = GoldColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = roleLabel.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color(0xFF8B6B28),
                fontWeight = FontWeight.Bold
            )
        }
    }
}