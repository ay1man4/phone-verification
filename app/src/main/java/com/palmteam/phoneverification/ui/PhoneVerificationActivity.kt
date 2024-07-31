package com.palmteam.phoneverification.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palmteam.verification.lib.PhoneVerificationManager
import com.palmteam.verification.lib.VerificationCallback

const val endpoint: String = "https://your-server-url.com"
val phoneVerificationManager = PhoneVerificationManager(endpoint)

class PhoneVerificationActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneVerificationScreen()
        }
    }
}

@Composable
fun PhoneVerificationScreen() {
    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isCodeSent) {
            Text("Enter Verification Code")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("Verification Code") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                phoneVerificationManager.verifyCode(
                    phoneNumber,
                    verificationCode,
                    object : VerificationCallback {
                        override fun onSuccess(customToken: String) {
                            message = "Verification successful! Custom token: $customToken"
                        }

                        override fun onFailure(error: String) {
                            message = "Verification failed: $error"
                        }
                    }
                )
            }) {
                Text("Verify Code")
            }
        } else {
            Text("Enter Phone Number")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                phoneVerificationManager.sendVerificationCode(
                    phoneNumber,
                    object : VerificationCallback {
                        override fun onSuccess(customToken: String) {
                            isCodeSent = true
                            message = "Code sent successfully!"
                        }

                        override fun onFailure(error: String) {
                            message = "Failed to send code: $error"
                        }
                    }
                )
            }) {
                Text("Send Verification Code")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message)
    }
}