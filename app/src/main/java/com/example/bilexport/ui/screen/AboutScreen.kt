package com.example.bilexport.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilexport.R
import com.example.bilexport.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.about_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.7f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text("ruruDown", fontSize = 36.sp, color = Blue500, fontWeight = FontWeight.Bold)
                Text("v1.0.0", color = TextSecondary, fontSize = 14.sp)
                Text("by Nagasaki Soyo", color = TextSecondary, fontSize = 13.sp)
                Text("\u00a9 2026 Nagasaki Soyo. All Rights Reserved.", color = TextDisabled, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkBackground.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SectionTitle("License \u2022 \u8F6F\u4EF6\u8BB8\u53EF") // Software License

                        Text("Freeware \u2022 \u514D\u8D39\u8F6F\u4EF6", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("This application is distributed as Freeware. Individuals are permitted to download, use and redistribute the official unmodified release free of charge.", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("\u672C\u8F6F\u4EF6\u4E3A\u514D\u8D39\u8F6F\u4EF6\uFF0C\u4F5C\u8005\u5141\u8BB8\u4EFB\u4F55\u4E2A\u4EBA\u514D\u8D39\u4E0B\u8F7D\u3001\u4F7F\u7528\u53CA\u5206\u4EAB\u5B98\u65B9\u53D1\u5E03\u7248\u672C\u3002", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Prohibited:", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Bullet("Use for commercial purposes")
                        Bullet("Sell, rent, sublicense or distribute for profit")
                        Bullet("Modify, repackage or redistribute for commercial use")
                        Bullet("Remove, alter or conceal copyright notices")
                        Bullet("Misrepresent yourself as the author")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("All copyrights and intellectual property rights remain the property of the author.", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(20.dp))

                        SectionTitle("Third-Party Components \u2022 \u7B2C\u4E09\u65B9\u7EC4\u4EF6")
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("FFmpeg", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("\u00a9 FFmpeg developers. Licensed under LGPL.", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Shizuku API", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("\u00a9 Shizuku Project Contributors. Used under its respective open-source license.", color = TextSecondary, fontSize = 12.sp)
                        Text("This application is not affiliated with the Shizuku project.", color = TextDisabled, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(20.dp))

                        SectionTitle("Disclaimer \u2022 \u514D\u8D23\u58F0\u660E")
                        Text("This software is provided \"AS IS\", without warranty of any kind. In no event shall the author be liable for any direct, indirect, incidental or consequential damages arising from the use of, or inability to use, this software. Use of this software is entirely at your own risk.", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("\u672C\u8F6F\u4EF6\u6309\"\u73B0\u72B6\"\u63D0\u4F9B\uFF0C\u4E0D\u63D0\u4F9B\u4EFB\u4F55\u5F62\u5F0F\u7684\u660E\u793A\u6216\u9ED8\u793A\u62C5\u4FDD\u3002\u4F5C\u8005\u4E0D\u5BF9\u56E0\u4F7F\u7528\u6216\u65E0\u6CD5\u4F7F\u7528\u672C\u8F6F\u4EF6\u800C\u5BFC\u81F4\u7684\u4EFB\u4F55\u76F4\u63A5\u3001\u95F4\u63A5\u3001\u5076\u7136\u6216\u540E\u679C\u6027\u635F\u5931\u627F\u62C5\u8D23\u4EFB\u3002", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(20.dp))

                        SectionTitle("Contact \u2022 \u8054\u7CFB\u65B9\u5F0F")
                        val ctx = LocalContext.current
                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Bilibili:", color = TextSecondary, fontSize = 13.sp)
                        Text("https://space.bilibili.com/292927431",
                            color = Blue500, fontSize = 13.sp, textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://space.bilibili.com/292927431"))
                                ctx.startActivity(i)
                            })
                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Email:", color = TextSecondary, fontSize = 13.sp)
                        Text("uminorysizuku@gmail.com",
                            color = Blue500, fontSize = 13.sp, textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val i = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:uminorysizuku@gmail.com"))
                                ctx.startActivity(i)
                            })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Blue500, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun Bullet(text: String) {
    Row(modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)) {
        Text("  \u2022  ", color = TextDisabled, fontSize = 13.sp)
        Text(text, color = TextSecondary, fontSize = 13.sp)
    }
}
