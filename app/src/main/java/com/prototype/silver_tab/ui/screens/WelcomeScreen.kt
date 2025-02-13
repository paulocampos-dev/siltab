package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.prototype.silver_tab.R
import androidx.constraintlayout.compose.Dimension as Dimensionc

@Composable
fun WelcomeScreen(
    onPDIButtonClicked: () -> Unit,
    onIniciarLojaButtonClicked: () -> Unit = {},
    onIniciarPlanoDeAcaoButtonClicked: () -> Unit = {},
    modifier: Modifier  = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
    ) {
        // Welcome Text
        Text(
            text = "Bem vindo, @Nome!",
            color = Color.White,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_small))
        )

        // Ready Text
        Text(
            text = "Pronto para começar?",
            color = Color.White,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height((dimensionResource(R.dimen.padding_large))))

        // PDI Button
        Button(
            onClick = onPDIButtonClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                //contentColor = Color.Unspecified
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            Box(
                modifier = Modifier.wrapContentSize(),
                //contentAlignment = Alignment.CenterStart
            ) {
                ConstraintLayout(
                    modifier = Modifier.wrapContentSize()
                ) {
                    val (button, car) = createRefs()

                    Image(
                        painter = painterResource(R.drawable.pdi_button),
                        contentDescription = "PDI Button",
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
                        contentDescription = "Car icon for stock button",
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .aspectRatio(2f)
                            .constrainAs(car) {
//                                width = Dimensionc.fillToConstraints
//                                height = Dimensionc.wrapContent
                                end.linkTo(button.end, margin = (0).dp)
                                bottom.linkTo(button.bottom, margin = 0.dp)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height((dimensionResource(R.dimen.padding_medium))))

        // Iniciar Loja Button
        /*Button(
            onClick = onIniciarLojaButtonClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                //contentColor = Color.Unspecified
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            Box(
                modifier = Modifier.wrapContentSize(),
            ) {
                ConstraintLayout(
                    modifier = Modifier.wrapContentSize()
                ) {
                    val (button, car) = createRefs()

                    Image(
                        painter = painterResource(R.drawable.nova_loja_button),
                        contentDescription = "New store button",
                        modifier = Modifier
                            .constrainAs(button) {
                                width = Dimension.wrapContent
                                height = Dimension.wrapContent
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )

                    Image(
                        painter = painterResource(R.drawable.dealer_store_image),
                        contentDescription = "Car icon for stock button",
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .aspectRatio(1.4f)
                            .constrainAs(car) {
                                end.linkTo(button.end, margin = (0).dp)
                                bottom.linkTo(button.bottom, margin = 0.dp)
                            }
                    )
                }
            }
        }*/

        Spacer(modifier = Modifier.height((dimensionResource(R.dimen.padding_small))))

        // Inicar Plano de Acao Button
    //        Button(
    //                onClick = onIniciarPlanoDeAcaoButtonClicked,
    //        colors = ButtonDefaults.buttonColors(
    //            containerColor = Color.Transparent,
    //            contentColor = Color.Unspecified
    //        ),
    //        shape = RectangleShape,
    //        modifier = Modifier.fillMaxWidth()
    //        ) {
    //        Box(
    //            modifier = Modifier.fillMaxWidth(),
    //            contentAlignment = Alignment.CenterStart
    //        ) {
    //            ConstraintLayout(
    //                modifier = Modifier.fillMaxWidth()
    //            ) {
    //                val (button) = createRefs()
    //
    //                Image(
    //                    painter = painterResource(R.drawable.action_plan_button),
    //                    contentDescription = "PDI Button",
    //                    modifier = Modifier
    //                        .fillMaxWidth()
    //                        .constrainAs(button) {
    //                            width = Dimensionc.fillToConstraints
    //                            height = Dimensionc.wrapContent
    //                            start.linkTo(parent.start)
    //                            end.linkTo(parent.end)
    //                        }
    //                )
    //            }
    //        }
    //    }
    }
}




@Preview(
    showBackground = true,
    backgroundColor = 0x272727
)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen({})
}