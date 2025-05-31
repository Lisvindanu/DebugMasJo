package com.example.kostkita.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kostkita.presentation.screens.auth.LoginScreen
import com.example.kostkita.presentation.screens.home.HomeScreen
import com.example.kostkita.presentation.screens.payment.PaymentListScreen
import com.example.kostkita.presentation.screens.payment.PaymentFormScreen
import com.example.kostkita.presentation.screens.room.RoomListScreen
import com.example.kostkita.presentation.screens.room.RoomFormScreen
import com.example.kostkita.presentation.screens.tenant.TenantListScreen
import com.example.kostkita.presentation.screens.tenant.TenantFormScreen
import kotlinx.coroutines.launch

@Composable
fun KostKitaNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = KostKitaScreens.Login.route
    ) {
        // Auth
        composable(route = KostKitaScreens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(KostKitaScreens.Home.route) {
                        popUpTo(KostKitaScreens.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(route = KostKitaScreens.Home.route) {
            HomeScreen(navController = navController)
        }

        // Tenant
        composable(route = KostKitaScreens.TenantList.route) {
            TenantListScreen(navController = navController)
        }
        composable(route = KostKitaScreens.TenantForm.route) {
            TenantFormScreen(navController = navController)
        }
        composable(route = "${KostKitaScreens.TenantForm.route}/{tenantId}") { backStackEntry ->
            val tenantId = backStackEntry.arguments?.getString("tenantId")
            TenantFormScreen(
                navController = navController,
                tenantId = tenantId
            )
        }

        // Room
        composable(route = KostKitaScreens.RoomList.route) {
            RoomListScreen(navController = navController)
        }
        composable(route = KostKitaScreens.RoomForm.route) {
            RoomFormScreen(navController = navController)
        }
        composable(route = "${KostKitaScreens.RoomForm.route}/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            RoomFormScreen(
                navController = navController,
                roomId = roomId
            )
        }

        // Payment
        composable(route = KostKitaScreens.PaymentList.route) {
            PaymentListScreen(navController = navController)
        }
        composable(route = KostKitaScreens.PaymentForm.route) {
            PaymentFormScreen(navController = navController)
        }
        composable(route = "${KostKitaScreens.PaymentForm.route}/{paymentId}") { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getString("paymentId")
            PaymentFormScreen(
                navController = navController,
                paymentId = paymentId
            )
        }
    }
}