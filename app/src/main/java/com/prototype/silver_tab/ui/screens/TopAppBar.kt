package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.R
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.viewmodels.DealerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
private object AppBarDefaults {
    val horizontalPadding = 4.dp
    val titleInsetWithoutIcon = Modifier.width(16.dp - horizontalPadding)
    val titleIconModifier = Modifier
        .wrapContentHeight()
        .width(52.dp - horizontalPadding)
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
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues())
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppBarDefaults.horizontalPadding, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (navigationIcon == null) {
                    Spacer(AppBarDefaults.titleInsetWithoutIcon)
                } else {
                    Row(AppBarDefaults.titleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                        navigationIcon()
                    }
                }

                Box(
                    Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    title()
                }

                Box(
                    Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        content = actions
                    )
                }
            }
        }
    }
}

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    showLocationInfo: Boolean = false,
    onLogoutButtonClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    onProfileButtonClicked: () -> Unit = {},
    navigateUp: () -> Unit = {},
    dealerViewModel: DealerViewModel = viewModel()
) {
    val strings = LocalStringResources.current
    val selectedDealer by dealerViewModel.selectedDealer.collectAsState()

    CustomAppBar(
        modifier = modifier,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BYDLogo()
                if (showLocationInfo) {
                    CurrentDateTime()
                    selectedDealer?.let { dealer ->
                        DealershipInfo(
                            dealerName = dealer.dealerName,
                            dealerAddress = "${dealer.region ?: ""} - ${dealer.dealerCode}"
                        )
                    } ?: DealershipInfo(
                        dealerName = strings.selectDealer,
                        dealerAddress = "-"
                    )
                }
            }
        },
        actions = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                ActionButtons(onLogoutButtonClicked, onProfileButtonClicked)
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
        modifier = Modifier
            .height(40.dp)
            .wrapContentWidth()
    )
}

@Composable
private fun CurrentDateTime() {
    val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    val currentDateTime = Calendar.getInstance().time

    Text(
        text = dateFormat.format(currentDateTime),
        style = MaterialTheme.typography.bodyMedium,
        color = AppBarDefaults.contentColor
    )
}

@Composable
private fun DealershipInfo(
    dealerName: String,
    dealerAddress: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = AppBarDefaults.contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))

        Column {
            Text(
                text = dealerName,
                style = MaterialTheme.typography.bodyMedium,
                color = AppBarDefaults.contentColor
            )
            Text(
                text = dealerAddress,
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
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onLogoutButtonClicked,
            modifier = Modifier.size(64.dp)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.logout),
                contentDescription = "Logout Button",
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
        IconButton(
            onClick = onProfileButtonClicked,
            modifier = Modifier.size(64.dp)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.profile_button),
                contentDescription = "Profile Button",
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
    }
}

@Composable
private fun CancelButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppBarDefaults.cancelButtonColor,
            contentColor = AppBarDefaults.contentColor
        ),
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = LocalStringResources.current.cancel,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
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