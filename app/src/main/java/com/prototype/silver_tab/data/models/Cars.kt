package com.prototype.silver_tab.data.models

import com.prototype.silver_tab.R

data class Car(
    val name: String? = null,
    val chassi: String? = null,
    val type: String? = null,
    val date: String? = null,
    val image: Int? = null,
    val route: String? =  null,
    val soc: Float? = null,
    val DE: Int? = null,
    val DD: Int? = null,
    val TD: Int? = null,
    val TE: Int? = null
)

val BydCars = listOf(
    Car(
        name = "BYD SHARK",
        type = "Híbrido",
        image = R.drawable.byd_shark,
    ),
    Car(
        name = "BYD KING DM-i",
        type = "Híbrido",
        image = R.drawable.byd_king,
    ),
    Car(
        name = "BYD SONG PLUS DM-i",
        type = "Híbrido",
        image = R.drawable.byd_song_plus,
    ),
    Car(
        name = "SONG PLUS PREMIUM DM-i",
        type = "Híbrido",
        //image = R.drawable.song_plus_premium,
        image = R.drawable.byd_song_plus,
    ),
    Car(
        name = "BYD SONG PRO DM-i",
        type = "Híbrido",
        image = R.drawable.byd_song_pro,
    ),
    Car(
        name = "BYD DOLPHIN MINI",
        type = "Elétrico",
        image = R.drawable.byd_dolphin_mini,
    ),
    Car(
        name = "BYD DOLPHIN",
        type = "Elétrico",
        image = R.drawable.byd_dolphin,
    ),
    Car(
        name = "BYD DOLPHIN PLUS",
        type = "Elétrico",
        image = R.drawable.byd_dolphin_plus,
    ),
    Car(
        name = "BYD HAN",
        type = "Elétrico",
        image = R.drawable.byd_han,
    ),
    Car(
        name = "BYD SEAL",
        type = "Elétrico",
        image = R.drawable.pid_car,
    ),
    Car(
        name = "BYD TAN",
        type = "Híbrido",
        image = R.drawable.byd_tan,
    ),
    Car(
        name = "BYD YUAN PLUS",
        type = "Elétrico",
        image = R.drawable.byd_yuan_plus,
    ),
    Car(
        name = "BYD YUAN PRO",
        type = "Elétrico",
        image = R.drawable.byd_yuan_pro,
    )
)
