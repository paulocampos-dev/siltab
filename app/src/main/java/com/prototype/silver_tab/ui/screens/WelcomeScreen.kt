package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.prototype.silver_tab.R
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.utils.LocalStringResources

@Composable
fun WelcomeScreen(
    onPDIButtonClicked: () -> Unit,
    onIniciarLojaButtonClicked: () -> Unit = {},
    onIniciarPlanoDeAcaoButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val strings = LocalStringResources.current
    val username by SilverTabApplication.userPreferences.username.collectAsState(initial = "")

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
    ) {
        // Welcome Text
        Text(
            text = strings.welcomeUserPrefix + username + "!",
            color = Color.White,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_small))
        )

        // Ready Text
        Text(
            text = strings.readyToStart,
            color = Color.White,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        // PDI Button
        Button(
            onClick = onPDIButtonClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                ConstraintLayout(
                    modifier = Modifier.wrapContentSize()
                ) {
                    val (button, car) = createRefs()

                    Image(
                        painter = painterResource(R.drawable.pdi_button),
                        contentDescription = strings.startInspection,
                        modifier = Modifier
                            .constrainAs(button) {
                                width = Dimension.wrapContent
                                height = Dimension.wrapContent
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )

                    Image(
                        painter = painterResource(R.drawable.pid_car),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .aspectRatio(2f)
                            .constrainAs(car) {
                                end.linkTo(button.end, margin = 0.dp)
                                bottom.linkTo(button.bottom, margin = 0.dp)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
    }
}