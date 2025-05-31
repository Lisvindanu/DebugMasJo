package com.example.kostkita.presentation.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kostkita.presentation.screens.room.RoomViewModel
import com.example.kostkita.presentation.screens.tenant.TenantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    navController: NavController,
    paymentId: String? = null,
    viewModel: PaymentViewModel = hiltViewModel(),
    tenantViewModel: TenantViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val payments by viewModel.payments.collectAsState()
    val tenants by tenantViewModel.tenants.collectAsState()
    val rooms by roomViewModel.rooms.collectAsState()

    val payment = payments.find { it.id == paymentId }

    var selectedTenantId by remember { mutableStateOf(payment?.tenantId ?: "") }
    var selectedRoomId by remember { mutableStateOf(payment?.roomId ?: "") }
    var bulanTahun by remember { mutableStateOf(payment?.bulanTahun ?: "") }
    var jumlahBayar by remember { mutableStateOf(payment?.jumlahBayar?.toString() ?: "") }
    var statusPembayaran by remember { mutableStateOf(payment?.statusPembayaran ?: "Lunas") }
    var denda by remember { mutableStateOf(payment?.denda?.toString() ?: "0") }

    var expandedTenant by remember { mutableStateOf(false) }
    var expandedRoom by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    val statusOptions = listOf("Lunas", "Belum Bayar", "Sebagian")

    val selectedTenant = tenants.find { it.id == selectedTenantId }
    val selectedRoom = rooms.find { it.id == selectedRoomId }

    // Auto-fill room when tenant is selected
    LaunchedEffect(selectedTenantId) {
        if (payment == null && selectedTenantId.isNotEmpty()) {
            val tenant = tenants.find { it.id == selectedTenantId }
            if (tenant?.roomId != null) {
                selectedRoomId = tenant.roomId
                val room = rooms.find { it.id == tenant.roomId }
                if (room != null) {
                    jumlahBayar = room.hargaBulanan.toString()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (paymentId == null) "Tambah Pembayaran" else "Edit Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tenant Selection
            ExposedDropdownMenuBox(
                expanded = expandedTenant,
                onExpandedChange = { expandedTenant = !expandedTenant }
            ) {
                OutlinedTextField(
                    value = selectedTenant?.nama ?: "Pilih Penghuni",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Penghuni") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTenant) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTenant,
                    onDismissRequest = { expandedTenant = false }
                ) {
                    tenants.forEach { tenant ->
                        DropdownMenuItem(
                            text = { Text(tenant.nama) },
                            onClick = {
                                selectedTenantId = tenant.id
                                expandedTenant = false
                            }
                        )
                    }
                }
            }

            // Room Selection
            ExposedDropdownMenuBox(
                expanded = expandedRoom,
                onExpandedChange = { expandedRoom = !expandedRoom }
            ) {
                OutlinedTextField(
                    value = selectedRoom?.let { "Kamar ${it.nomorKamar}" } ?: "Pilih Kamar",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kamar") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoom) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRoom,
                    onDismissRequest = { expandedRoom = false }
                ) {
                    rooms.filter { it.statusKamar == "Terisi" }.forEach { room ->
                        DropdownMenuItem(
                            text = { Text("Kamar ${room.nomorKamar} - ${room.tipeKamar}") },
                            onClick = {
                                selectedRoomId = room.id
                                expandedRoom = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = bulanTahun,
                onValueChange = { bulanTahun = it },
                label = { Text("Bulan/Tahun (contoh: Januari 2025)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = jumlahBayar,
                onValueChange = { jumlahBayar = it.filter { char -> char.isDigit() } },
                label = { Text("Jumlah Bayar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Status Selection
            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = statusPembayaran,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status Pembayaran") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    statusOptions.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                statusPembayaran = status
                                expandedStatus = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = denda,
                onValueChange = { denda = it.filter { char -> char.isDigit() } },
                label = { Text("Denda (opsional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (payment == null) {
                        viewModel.addPayment(
                            tenantId = selectedTenantId,
                            roomId = selectedRoomId,
                            bulanTahun = bulanTahun,
                            jumlahBayar = jumlahBayar.toIntOrNull() ?: 0,
                            statusPembayaran = statusPembayaran,
                            denda = denda.toIntOrNull() ?: 0
                        )
                    } else {
                        viewModel.updatePayment(
                            payment.copy(
                                tenantId = selectedTenantId,
                                roomId = selectedRoomId,
                                bulanTahun = bulanTahun,
                                jumlahBayar = jumlahBayar.toIntOrNull() ?: 0,
                                statusPembayaran = statusPembayaran,
                                denda = denda.toIntOrNull() ?: 0
                            )
                        )
                    }
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedTenantId.isNotBlank() &&
                        selectedRoomId.isNotBlank() &&
                        bulanTahun.isNotBlank() &&
                        jumlahBayar.isNotBlank()
            ) {
                Text(if (payment == null) "Simpan" else "Update")
            }
        }
    }
}