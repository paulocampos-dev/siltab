package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.auth.AuthResult
import com.prototype.silver_tab.ui.components.LanguageSelector
import com.prototype.silver_tab.utils.Language
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.LocalizationManager
import com.prototype.silver_tab.utils.chineseStrings
import com.prototype.silver_tab.utils.englishStrings
import com.prototype.silver_tab.utils.portugueseStrings
import com.prototype.silver_tab.viewmodels.DealerViewModel
import com.prototype.silver_tab.viewmodels.LoginViewModel


@Composable
fun LoginScreen(
    onLoginButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginState by viewModel.loginState.collectAsState()

    // Handle login state
    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthResult.Success<*> -> {
                onLoginButtonClicked()
                viewModel.clearLoginState()
            }
            is AuthResult.Error<*> -> {
                // Show error message
            }
            else -> {}
        }
    }

    val currentLanguage by LocalizationManager.currentLanguage.collectAsState()

    val strings = when (currentLanguage) {
        Language.ENGLISH -> englishStrings
        Language.PORTUGUESE -> portugueseStrings
        Language.CHINESE -> chineseStrings
    }

    val dealerViewModel: DealerViewModel = viewModel()
    CompositionLocalProvider(LocalStringResources provides strings) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            // Language selector
            LanguageSelector(
                onLanguageSelected = { language ->
                    LocalizationManager.setLanguage(language)
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Image(
                painter = painterResource(R.drawable.bgate_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(240.dp)
                    .padding(top = 64.dp)
                    .align(Alignment.CenterHorizontally)
//                    .border(BorderStroke(10.dp, Color.White))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .padding(bottom = 128.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.updateUsername(it) },
                    label = { Text(strings.email) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint =Color.White) },
                    placeholder = { Text(strings.email) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text(strings.password) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint =Color.White) },
                    placeholder = { Text(strings.password) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login button with loading state
                Button(
                    onClick = { viewModel.login(dealerViewModel) },
                    enabled = loginState !is AuthResult.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E24AA),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (loginState is AuthResult.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(text = strings.login, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Error message
                if (loginState is AuthResult.Error<*>) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.loginError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}