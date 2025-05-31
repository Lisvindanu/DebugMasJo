package com.example.kostkita.presentation.navigation

sealed class KostKitaScreens(val route: String) {
    object Login : KostKitaScreens("login")
    object Home : KostKitaScreens("home")
    object TenantList : KostKitaScreens("tenant_list")
    object TenantForm : KostKitaScreens("tenant_form")
    object RoomList : KostKitaScreens("room_list")
    object RoomForm : KostKitaScreens("room_form")
    object PaymentList : KostKitaScreens("payment_list")
    object PaymentForm : KostKitaScreens("payment_form")
}