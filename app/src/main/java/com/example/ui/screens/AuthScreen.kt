package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.util.Log
import com.example.ui.viewmodel.DoraViewModel
import com.example.ui.LogoConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: DoraViewModel,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Configure Google Sign-In to open Account Picker and show device accounts
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    // Standard Activity launcher for Google Account Picker
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val chosenEmail = account.email ?: ""
                val chosenName = account.displayName ?: "Google User"
                val photoUrl = account.photoUrl?.toString() ?: "https://lh3.googleusercontent.com/a/default-user"

                Log.d("FirebaseAuthDebug", "Google picker auth success. User selected: Email=$chosenEmail, Name=$chosenName")
                viewModel.loginWithGoogle(
                    email = chosenEmail,
                    name = chosenName,
                    picUrl = photoUrl
                )
            } else {
                errorMessage = "Google accounts picker response was empty."
                Log.e("FirebaseAuthDebug", "GoogleSignInAccount task was null")
            }
        } catch (e: ApiException) {
            val statusMessage = when (e.statusCode) {
                12501 -> "Google Sign-In was cancelled by the user."
                12502 -> "Google Sign-In is already in progress."
                else -> "Google accounts picker error: Code ${e.statusCode}"
            }
            errorMessage = statusMessage
            Log.e("FirebaseAuthDebug", "Google Sign in failure code=${e.statusCode}", e)
        } catch (e: Exception) {
            errorMessage = "Google authentication failed: ${e.localizedMessage}"
            Log.e("FirebaseAuthDebug", "Google Sign-InApiException", e)
        }
    }

    val authLoading by viewModel.authLoading.collectAsState()
    val authSuccess by viewModel.authSuccess.collectAsState()
    val authStateMessage by viewModel.authStateMessage.collectAsState()

    // Sync state feedback messages with local view state message
    LaunchedEffect(authStateMessage) {
        authStateMessage?.let {
            errorMessage = it
        }
    }

    // Reset status on screen entry/exit
    DisposableEffect(Unit) {
        viewModel.clearAuthStatus()
        onDispose {
            viewModel.clearAuthStatus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                    )
                )
            )
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium Centered Logo Integration
            LogoConfig.DoraWordmarkLogo(
                iconSize = 72.dp,
                textSize = 34.sp,
                subtitleSize = 12.sp,
                textColor = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Your digital magazine & tech trend observer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Main Auth container Card
            ElevatedCard(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_container_card")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSignUp) "Create Account" else "Welcome Back",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = if (errorMessage.contains("successfully") || errorMessage.contains("sent"))
                                Color(0xFF4CAF50)
                            else
                                MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AlternateEmail,
                                contentDescription = "EmailIcon"
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("email_input")
                    )

                    // Nickname / Full Name field for Signup
                    AnimatedVisibility(
                        visible = isSignUp,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = {
                                nickname = it
                                errorMessage = ""
                            },
                            label = { Text("Display Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "NameIcon"
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("name_input")
                        )
                    }

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it 
                            errorMessage = ""
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "PasswordIcon"
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Confirm Password Input (visible only for SignUp)
                    AnimatedVisibility(
                        visible = isSignUp,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                errorMessage = ""
                            },
                            label = { Text("Confirm Password Details") },
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "ConfirmPasswordIcon"
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("confirm_password_input")
                        )
                    }

                    // Forgot Password link for logged-out emails
                    if (!isSignUp) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Forgot password?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable {
                                        if (email.isBlank()) {
                                            errorMessage = "Please type your email address above first."
                                        } else {
                                            viewModel.sendPasswordResetEmail(email)
                                        }
                                    }
                                    .testTag("forgot_password_btn")
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Get started / Continue button below
                    Button(
                        onClick = {
                            val trimmedEmail = email.trim()
                            if (trimmedEmail.isBlank() || password.isBlank() || (isSignUp && (nickname.isBlank() || confirmPassword.isBlank()))) {
                                errorMessage = "Please fill in all requested fields."
                                return@Button
                            }
                            if (isSignUp) {
                                if (password.length < 6) {
                                    errorMessage = "Password must be at least 6 characters long."
                                    return@Button
                                }
                                if (!password.any { it.isDigit() }) {
                                    errorMessage = "Weak password: Must contain at least one number."
                                    return@Button
                                }
                                if (!password.any { it.isUpperCase() }) {
                                    errorMessage = "Weak password: Must contain at least one uppercase letter."
                                    return@Button
                                }
                                if (!password.any { it.isLowerCase() }) {
                                    errorMessage = "Weak password: Must contain at least one lowercase letter."
                                    return@Button
                                }
                                val specialChars = "@#$%^&+=!_\\-*./?|()'\";:,<>`~"
                                if (!password.any { it in specialChars }) {
                                    errorMessage = "Weak password: Must contain at least one special character (e.g., @, #, $, etc.)."
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    errorMessage = "Passwords do not match."
                                    return@Button
                                }
                            }
                            errorMessage = ""
                            if (isSignUp) {
                                viewModel.signUpWithEmailAndPassword(trimmedEmail, nickname, password, confirmPassword)
                            } else {
                                viewModel.loginWithEmailAndPassword(trimmedEmail, password)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !authLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_btn")
                    ) {
                        if (authLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isSignUp) "Sign Up & Read" else "Continue with Email",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = "OR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign In Button - shows Google accounts list on the device for authentication flow
                    OutlinedButton(
                        onClick = {
                            errorMessage = ""
                            try {
                                Log.d("FirebaseAuthDebug", "Continue with Google clicked. Launching Google accounts picker.")
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            } catch (e: Exception) {
                                errorMessage = "Unable to start Google Sign In flow: ${e.localizedMessage}"
                                Log.e("FirebaseAuthDebug", "Failed to launch google accounts picker launcher", e)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        enabled = !authLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("google_login_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Stylized Google premium vector represent
                            Text(
                                text = "G ",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Text(
                                text = " Continue with Google",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mode Toggle Screen Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSignUp) "Already have an account?" else "New to Dora Library?",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSignUp) "Log In" else "Create one",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    isSignUp = !isSignUp
                                    errorMessage = ""
                                    viewModel.clearAuthStatus()
                                }
                                .testTag("toggle_auth_mode")
                        )
                    }
                }
            }
        }
    }
}
