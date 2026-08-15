package com.weiting.mydays.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weiting.mydays.ui.component.NavBarBackground
import com.weiting.mydays.ui.component.SettingActionItem
import com.weiting.mydays.ui.component.SettingContentColor
import com.weiting.mydays.ui.component.SettingDivider
import com.weiting.mydays.ui.component.SettingGroup
import com.weiting.mydays.ui.component.SettingNavigationItem
import com.weiting.mydays.ui.component.SettingSectionHeader

@Composable
fun SettingScreen(
    onOpenFeatures: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        // Profile Header
        ProfileHeader(name = "Weiting", email = "weiting@example.com")

        Spacer(modifier = Modifier.height(28.dp))

        // Features Section
        SettingSectionHeader("功能")
        SettingGroup {
            SettingNavigationItem(
                icon = Icons.Default.Apps,
                title = "功能中心",
                onClick = onOpenFeatures
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Account Settings
        SettingSectionHeader("帳號")
        SettingGroup {
            SettingNavigationItem(
                icon = Icons.Default.Person,
                title = "個人資料",
                onClick = {}
            )
            SettingDivider()
            SettingNavigationItem(
                icon = Icons.Default.Lock,
                title = "密碼與安全性",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout
        SettingGroup {
            SettingActionItem(
                icon = Icons.Default.ExitToApp,
                title = "登出",
                onClick = onLogout,
                destructive = true
            )
        }
    }
}

@Composable
private fun ProfileHeader(name: String, email: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = SettingContentColor,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = name, color = SettingContentColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = email, color = SettingContentColor.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}

@Preview(name = "Setting", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun SettingScreenPreview() {
    Box(modifier = Modifier.fillMaxSize().background(NavBarBackground.Mint.brush)) {
        SettingScreen(onOpenFeatures = {}, onLogout = {})
    }
}
