package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.prototype.silver_tab.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onLogoutButtonClicked: () -> Unit = {},
    onProfileButtonClicked: () -> Unit = {},
    navigateUp: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.byd_white_logo),
                contentDescription = "BYD Logo",
                contentScale = ContentScale.Fit,
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0xFF212121)
        ),
        modifier = modifier,
        actions = {
            IconButton(
                onClick = onLogoutButtonClicked
            ) {
                Image(
                    ImageVector.vectorResource(R.drawable.logout),
                    contentDescription = "Logout Button"
                )
            }
            IconButton(
                onClick = onProfileButtonClicked
            ) {
                Image(
                    ImageVector.vectorResource(R.drawable.profile_button),
                    contentDescription = "Profile Button"
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun AppBarPreview() {
    AppBar(
        canNavigateBack = false,
        navigateUp = {}
    )

}