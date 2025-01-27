package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prototype.silver_tab.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private object AppBarDefaults {
    val horizontalPadding = 4.dp
    val titleInsetWithoutIcon = Modifier.width(16.dp - horizontalPadding)
    val titleIconModifier = Modifier
        .wrapContentHeight()
        .width(72.dp - horizontalPadding)
    val backgroundColor = Color(0xFF212121)
    val contentColor = Color.White
    val cancelButtonColor = Color(0xFFD32F2F)
}

@Composable
fun CustomAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = AppBarDefaults.backgroundColor,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 4.dp
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = elevation,
        modifier = modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppBarDefaults.horizontalPadding, vertical = 8.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (navigationIcon == null) {
                Spacer(AppBarDefaults.titleInsetWithoutIcon)
            } else {
                Row(AppBarDefaults.titleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                    navigationIcon()
                }
            }

            Row(
                Modifier.wrapContentHeight().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                title()
            }

            Row(
                Modifier.wrapContentHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    showLocationInfo: Boolean = false,
    dealershipName: String = "Nome da Concessionária",
    dealershipAddress: String = "Endereço da Concessionária",
    onLogoutButtonClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    onProfileButtonClicked: () -> Unit = {},
    navigateUp: () -> Unit = {},
) {
    CustomAppBar(
        modifier = modifier.wrapContentHeight(),
        navigationIcon = if (canNavigateBack) {
            {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back",
                        tint = AppBarDefaults.contentColor
                    )
                }
            }
        } else null,
        title = {
            if (showLocationInfo) {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BYDLogo()
                    Spacer(modifier = Modifier.height(8.dp))
                    CurrentDateTime()
                    DealershipInfo(dealershipName, dealershipAddress)
                }
            } else {
                BYDLogo()
            }
        },
        actions = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ActionButtons(onLogoutButtonClicked, onProfileButtonClicked)
                Spacer(modifier = Modifier.height(8.dp))
                if (showLocationInfo) {
                    CancelButton(onCancelClicked)
                }
            }
        }
    )
}

@Composable
private fun BYDLogo() {
    Image(
        painter = painterResource(R.drawable.byd_white_logo),
        contentDescription = "BYD Logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier.wrapContentSize()
    )
}

@Composable
private fun CurrentDateTime() {
    val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    val currentDateTime = Calendar.getInstance().time

    Text(
        text = dateFormat.format(currentDateTime),
        style = MaterialTheme.typography.bodySmall,
        color = AppBarDefaults.contentColor
    )
}

@Composable
private fun DealershipInfo(dealershipName: String, dealershipAddress: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = AppBarDefaults.contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))

        Column {
            Text(
                text = dealershipName,
                style = MaterialTheme.typography.bodyMedium,
                color = AppBarDefaults.contentColor
            )
            Text(
                text = dealershipAddress,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onLogoutButtonClicked: () -> Unit,
    onProfileButtonClicked: () -> Unit
) {
    Row {
        IconButton(onClick = onLogoutButtonClicked) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.logout),
                contentDescription = "Logout Button"
            )
        }
        IconButton(onClick = onProfileButtonClicked) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.profile_button),
                contentDescription = "Profile Button"
            )
        }
    }
}

@Composable
private fun CancelButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = AppBarDefaults.contentColor,
            containerColor = AppBarDefaults.cancelButtonColor
        )
    ) {
        Text(
            text = "CANCELAR",
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview
@Composable
fun TopAppBarPreview() {
    AppBar(
        canNavigateBack = false,
        navigateUp = {}
    )
}

@Preview
@Composable
fun TopAppBarWithLocationInfoPreview() {
    AppBar(
        canNavigateBack = false,
        showLocationInfo = true,
        navigateUp = {}
    )
}