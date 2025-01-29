package com.prototype.silver_tab.data.models

import com.prototype.silver_tab.R

val fakeCarList = listOf(
    Car(
        name = "BYD SHARK",
        chassi = "LC0C14DK7P0123456",
        type = "Híbrido",
        date = "2025-01-15",
        image = R.drawable.byd_shark,
        route = "/shark",
        soc = 85.5f,
        DE = 32,
        DD = 35,
        TD = 33,
        TE = 34
    ),
    Car(
        name = "BYD KING DM-i",
        chassi = "LC0C14DK7P0234567",
        type = "Híbrido",
        date = "2025-01-16",
        image = R.drawable.byd_king,
        route = "/king",
        soc = 92.0f,
        DE = 35,
        DD = 34,
        TD = 36,
        TE = 35
    ),
    Car(
        name = "BYD SONG PLUS DM-i",
        chassi = "LC0C14DK7P0345678",
        type = "Híbrido",
        date = "2025-01-17",
        image = R.drawable.byd_song_plus,
        route = "/song_plus",
        soc = 88.5f,
        DE = 33,
        DD = 32,
        TD = 34,
        TE = 33
    ),
    Car(
        name = "SONG PLUS PREMIUM DM-i",
        chassi = "LC0C14DK7P0456789",
        type = "Híbrido",
        date = "2024-01-18",
        // image = R.drawable.song_plus_premium, nao consegui essa imagem
        image = R.drawable.byd_song_plus,
        route = "/song_plus_premium",
        soc = 95.0f,
        DE = 36,
        DD = 35,
        TD = 35,
        TE = 36
    ),
    Car(
        name = "BYD SONG PRO DM-i",
        chassi = "LC0C14DK7P0567890",
        type = "Híbrido",
        date = "2024-01-19",
        image = R.drawable.byd_song_pro,
        route = "/song_pro",
        soc = 90.0f,
        DE = 34,
        DD = 33,
        TD = 35,
        TE = 34
    ),
    Car(
        name = "BYD DOLPHIN MINI",
        chassi = "LC0C14DK7P0678901",
        type = "Elétrico",
        date = "2024-01-20",
        image = R.drawable.byd_dolphin_mini,
        route = "/dolphin_mini",
        soc = 87.5f,
        DE = 31,
        DD = 32,
        TD = 31,
        TE = 32
    ),
    Car(
        name = "BYD DOLPHIN",
        chassi = "LC0C14DK7P0789012",
        type = "Elétrico",
        date = "2024-01-21",
        image = R.drawable.byd_dolphin,
        route = "/dolphin",
        soc = 89.0f,
        DE = 33,
        DD = 34,
        TD = 33,
        TE = 34
    ),
    Car(
        name = "BYD DOLPHIN PLUS",
        chassi = "LC0C14DK7P0890123",
        type = "Elétrico",
        date = "2024-01-22",
        image = R.drawable.byd_dolphin_plus,
        route = "/dolphin_plus",
        soc = 91.5f,
        DE = 35,
        DD = 34,
        TD = 35,
        TE = 34
    ),
    Car(
        name = "BYD HAN",
        chassi = "LC0C14DK7P0901234",
        type = "Elétrico",
        date = "2024-01-23",
        image = R.drawable.byd_han,
        route = "/han",
        soc = 93.5f,
        DE = 36,
        DD = 35,
        TD = 36,
        TE = 35
    ),
    Car(
        name = "BYD SEAL",
        chassi = "LC0C14DK7P0012345",
        type = "Elétrico",
        date = "2024-01-24",
        image = R.drawable.pid_car,
        route = "/seal",
        soc = 94.0f,
        DE = 35,
        DD = 36,
        TD = 35,
        TE = 36
    ),
    Car(
        name = "BYD TAN",
        chassi = "LC0C14DK7P0123450",
        type = "Híbrido",
        date = "2024-01-25",
        image = R.drawable.byd_tan,
        route = "/tan",
        soc = 87.0f,
        DE = 33,
        DD = 34,
        TD = 33,
        TE = 34
    ),
    Car(
        name = "BYD YUAN PLUS",
        chassi = "LC0C14DK7P0234501",
        type = "Elétrico",
        date = "2024-01-26",
        image = R.drawable.byd_yuan_plus,
        route = "/yuan_plus",
        soc = 88.0f,
        DE = 32,
        DD = 33,
        TD = 32,
        TE = 33
    ),
    Car(
        name = "BYD YUAN PRO",
        chassi = "LC0C14DK7P0345012",
        type = "Elétrico",
        date = "2024-01-27",
        image = R.drawable.byd_yuan_pro,
        route = "/yuan_pro",
        soc = 86.5f,
        DE = 31,
        DD = 32,
        TD = 31,
        TE = 32
    )
)

val mockProfile = Profile(name = "Felipe",
    "Teixeira",
    "Felipe.Teixeira@byd.com",
    "Estagiário")