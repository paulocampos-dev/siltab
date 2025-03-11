package com.prototype.silver_tab.ui.screens


import android.util.Log
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.BuildConfig
import com.prototype.silver_tab.R
import com.prototype.silver_tab.ui.components.LanguageSelector
import com.prototype.silver_tab.utils.Language
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.LocalizationManager
import com.prototype.silver_tab.viewmodels.DealerViewModel
import com.prototype.silver_tab.viewmodels.LoginViewModel

import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.prototype.silver_tab.logging.testarLog





@Composable
fun LoginScreen(
    onLoginButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val authState by viewModel.loginState.collectAsState(initial = null)
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Create focus manager to control focus
    val focusManager = LocalFocusManager.current

    // Add keyboard controller to hide keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // Create focus requesters for each field
    val passwordFocusRequester = remember { FocusRequester() }

    // Handle login state changes
    LaunchedEffect(authState) {
        if (authState?.isAuthenticated == true) {
            onLoginButtonClicked()
        }
    }

    val strings = LocalStringResources.current
    val dealerViewModel: DealerViewModel = viewModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            // Language selector at the top
            LanguageSelector(
                onLanguageSelected = { language ->
                    LocalizationManager.setLanguage(language)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 16.dp)
            )

            Text(BuildConfig.BASE_URL)

            // Logo with flexible spacing
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.bgate_logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.weight(0.1f))

            // Login form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.updateUsername(it) },
                    label = { Text(strings.email) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
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
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        }
                    ),
                    singleLine = true
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text(strings.password) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                    placeholder = { Text(strings.password) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Lock else Icons.Outlined.Lock,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
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
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            viewModel.login(dealerViewModel)
                        }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Login button with loading state
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.login(dealerViewModel)
                    },
                    enabled = !(authState?.isLoading ?: false),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E24AA),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (authState?.isLoading == true) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(text = strings.login, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Error message
                if (authState?.error != null) {
                    Text(
                        text = strings.loginError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Add a spacer that will push content up when keyboard appears
            Spacer(modifier = Modifier.weight(0.3f))
        }
    }
}

