// File: presentation/screens/home/HomeScreen.kt
package com.example.kostkita.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kostkita.domain.model.Payment
import com.example.kostkita.domain.model.Room
import com.example.kostkita.domain.model.Tenant
import com.example.kostkita.presentation.navigation.KostKitaScreens
import com.example.kostkita.presentation.screens.room.RoomViewModel
import com.example.kostkita.presentation.screens.tenant.TenantViewModel
import com.example.kostkita.presentation.screens.payment.PaymentViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    tenantViewModel: TenantViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {
    val tenants by tenantViewModel.tenants.collectAsState()
    val rooms by roomViewModel.rooms.collectAsState()
    val payments by paymentViewModel.payments.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Statistik", "Aktivitas")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Header
                item {
                    HeaderSection()
                }

                // Tab Row
                item {
                    TabSection(
                        selectedTab = selectedTab,
                        tabs = tabs,
                        onTabSelected = { selectedTab = it }
                    )
                }

                // Content based on selected tab
                when (selectedTab) {
                    0 -> {
                        item {
                            StatsSection(
                                totalTenants = tenants.size,
                                occupiedRooms = rooms.count { it.statusKamar.lowercase() == "terisi" },
                                totalRooms = rooms.size,
                                monthlyIncome = calculateMonthlyIncome(payments)
                            )
                        }

                        item {
                            QuickActionsSection(navController)
                        }

                        item {
                            ActivityFeedSection(
                                tenants = tenants.takeLast(5),
                                payments = payments.takeLast(5)
                            )
                        }
                    }
                    1 -> {
                        item {
                            StatisticsSection(rooms)
                        }
                    }
                    2 -> {
                        item {
                            ActivityTimelineSection(tenants, payments)
                        }
                    }
                }
            }

            // Floating Navigation Bar
            FloatingBottomNavigation(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun HeaderSection() {
    var visible by remember { mutableStateOf(false) }
    val greeting = getGreeting()

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "KostKita",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(Date()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { /* Profile */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TabSection(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.padding(vertical = 16.dp),
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                TabItem(
                    title = title,
                    isSelected = selectedTab == index
                )
            }
        }
    }
}

@Composable
private fun TabItem(title: String, isSelected: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatsSection(
    totalTenants: Int,
    occupiedRooms: Int,
    totalRooms: Int,
    monthlyIncome: Long
) {
    val stats = listOf(
        StatItem(
            icon = Icons.Default.Groups,
            title = "Total Penghuni",
            value = totalTenants.toString(),
            color = Color(0xFF6366F1),
            trend = "+${(1..5).random()}%"
        ),
        StatItem(
            icon = Icons.Default.MeetingRoom,
            title = "Kamar Terisi",
            value = "$occupiedRooms/$totalRooms",
            color = Color(0xFF8B5CF6),
            trend = "${((occupiedRooms.toFloat() / totalRooms) * 100).toInt()}%"
        ),
        StatItem(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            title = "Pendapatan",
            value = formatRupiahCompact(monthlyIncome),
            color = Color(0xFF10B981),
            trend = "+12%"
        ),
        StatItem(
            icon = Icons.Default.CalendarToday,
            title = "Jatuh Tempo",
            value = "${(1..5).random()}",
            color = Color(0xFFF59E0B),
            trend = "Hari ini"
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stats.size) { index ->
            StatCard(
                stat = stats[index],
                index = index
            )
        }
    }
}

@Composable
private fun StatCard(stat: StatItem, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 100L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = stat.color.copy(alpha = 0.1f)
            ),
            onClick = { /* Navigate to detail */ }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = stat.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(stat.color.copy(alpha = 0.2f))
                            .padding(6.dp),
                        tint = stat.color
                    )

                    Badge(
                        containerColor = stat.color.copy(alpha = 0.2f),
                        contentColor = stat.color
                    ) {
                        Text(
                            text = stat.trend,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column {
                    Text(
                        text = stat.value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = stat.color
                    )
                    Text(
                        text = stat.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Aksi Cepat",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PersonAdd,
                label = "Tambah\nPenghuni",
                onClick = { navController.navigate(KostKitaScreens.TenantForm.route) }
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AddHome,
                label = "Tambah\nKamar",
                onClick = { navController.navigate(KostKitaScreens.RoomForm.route) }
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Receipt,
                label = "Catat\nBayar",
                onClick = { navController.navigate(KostKitaScreens.PaymentForm.route) }
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    Card(
        onClick = {
            pressed = true
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (pressed) 0.dp else 4.dp,
            pressedElevation = 0.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(if (pressed) 0.95f else 1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ActivityFeedSection(
    tenants: List<Tenant>,
    payments: List<Payment>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Aktivitas Terkini",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Activity cards
        val activities = (tenants.map { ActivityData.Tenant(it) } +
                payments.map { ActivityData.Payment(it) })
            .sortedByDescending {
                when (it) {
                    is ActivityData.Tenant -> it.tenant.tanggalMasuk
                    is ActivityData.Payment -> it.payment.tanggalBayar
                }
            }
            .take(5)

        activities.forEachIndexed { index, activity ->
            ActivityCard(activity = activity, index = index)
            if (index < activities.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ActivityCard(
    activity: ActivityData,
    index: Int
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 150L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            when (activity) {
                is ActivityData.Tenant -> TenantActivityItem(activity.tenant)
                is ActivityData.Payment -> PaymentActivityItem(activity.payment)
            }
        }
    }
}

@Composable
private fun TenantActivityItem(tenant: Tenant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tenant.nama.take(2).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tenant.nama,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Penghuni baru • ${formatDateRelative(tenant.tanggalMasuk)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            Icons.Default.PersonAdd,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PaymentActivityItem(payment: Payment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    color = if (payment.statusPembayaran == "Lunas")
                        Color(0xFF10B981).copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (payment.statusPembayaran == "Lunas")
                    Icons.Default.CheckCircle
                else
                    Icons.Default.Schedule,
                contentDescription = null,
                tint = if (payment.statusPembayaran == "Lunas")
                    Color(0xFF10B981)
                else
                    MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Pembayaran ${payment.bulanTahun}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${formatRupiah(payment.jumlahBayar)} • ${formatDateRelative(payment.tanggalBayar)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Badge(
            containerColor = if (payment.statusPembayaran == "Lunas")
                Color(0xFF10B981).copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
            contentColor = if (payment.statusPembayaran == "Lunas")
                Color(0xFF10B981)
            else
                MaterialTheme.colorScheme.error
        ) {
            Text(
                text = payment.statusPembayaran,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun StatisticsSection(rooms: List<Room>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Statistik Hunian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                val occupied = rooms.count { it.statusKamar.lowercase() == "terisi" }
                val available = rooms.count { it.statusKamar.lowercase() == "tersedia" }
                val maintenance = rooms.size - occupied - available

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        label = "Terisi",
                        value = occupied,
                        color = Color(0xFF10B981),
                        percentage = (occupied.toFloat() / rooms.size * 100).toInt()
                    )
                    StatisticItem(
                        label = "Tersedia",
                        value = available,
                        color = Color(0xFF3B82F6),
                        percentage = (available.toFloat() / rooms.size * 100).toInt()
                    )
                    StatisticItem(
                        label = "Maintenance",
                        value = maintenance,
                        color = Color(0xFFF59E0B),
                        percentage = (maintenance.toFloat() / rooms.size * 100).toInt()
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: Int,
    color: Color,
    percentage: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActivityTimelineSection(
    tenants: List<Tenant>,
    payments: List<Payment>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Timeline Aktivitas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Simple timeline of recent activities
        val allActivities = buildList {
            tenants.forEach { tenant ->
                add(TimelineEvent(
                    timestamp = tenant.tanggalMasuk,
                    title = "Penghuni Baru",
                    description = "${tenant.nama} bergabung",
                    icon = Icons.Default.PersonAdd,
                    color = MaterialTheme.colorScheme.primary
                ))
            }
            payments.forEach { payment ->
                add(TimelineEvent(
                    timestamp = payment.tanggalBayar,
                    title = "Pembayaran",
                    description = "${payment.bulanTahun} - ${formatRupiah(payment.jumlahBayar)}",
                    icon = Icons.Default.AttachMoney,
                    color = if (payment.statusPembayaran == "Lunas") Color(0xFF10B981) else MaterialTheme.colorScheme.error
                ))
            }
        }.sortedByDescending { it.timestamp }.take(5)

        allActivities.forEach { event ->
            TimelineItem(event = event)
        }
    }
}

@Composable
private fun TimelineItem(event: TimelineEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(event.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = event.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = event.color
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatDateRelative(event.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FloatingBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .height(64.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard",
                selected = true,
                onClick = { }
            )
            NavItem(
                icon = Icons.Default.Person,
                label = "Penghuni",
                selected = false,
                onClick = { navController.navigate(KostKitaScreens.TenantList.route) }
            )
            NavItem(
                icon = Icons.Default.MeetingRoom,
                label = "Kamar",
                selected = false,
                onClick = { navController.navigate(KostKitaScreens.RoomList.route) }
            )
            NavItem(
                icon = Icons.Default.Receipt,
                label = "Bayar",
                selected = false,
                onClick = { navController.navigate(KostKitaScreens.PaymentList.route) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier.scale(scale)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Data classes
data class StatItem(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val color: Color,
    val trend: String
)

sealed class ActivityData {
    data class Tenant(val tenant: com.example.kostkita.domain.model.Tenant) : ActivityData()
    data class Payment(val payment: com.example.kostkita.domain.model.Payment) : ActivityData()
}

data class TimelineEvent(
    val timestamp: Long,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

// Helper functions
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Selamat Pagi ☀️"
        hour < 15 -> "Selamat Siang 🌤️"
        hour < 18 -> "Selamat Sore 🌅"
        else -> "Selamat Malam 🌙"
    }
}

private fun calculateMonthlyIncome(payments: List<Payment>): Long {
    val currentMonth = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())
    return payments
        .filter { it.bulanTahun.contains(currentMonth) && it.statusPembayaran == "Lunas" }
        .sumOf { it.jumlahBayar.toLong() }
}

private fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}

private fun formatRupiahCompact(amount: Long): String {
    return when {
        amount >= 1_000_000_000 -> "Rp ${amount / 1_000_000_000}M"
        amount >= 1_000_000 -> "Rp ${amount / 1_000_000}jt"
        amount >= 1_000 -> "Rp ${amount / 1_000}rb"
        else -> "Rp $amount"
    }
}

private fun formatDateRelative(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        days == 0L -> "hari ini"
        days == 1L -> "kemarin"
        days < 7 -> "$days hari lalu"
        days < 30 -> "${days / 7} minggu lalu"
        else -> SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(timestamp))
    }
}