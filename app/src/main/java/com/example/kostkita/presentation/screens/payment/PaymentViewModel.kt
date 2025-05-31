package com.example.kostkita.presentation.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostkita.domain.model.Payment
import com.example.kostkita.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPayments()
    }

    private fun loadPayments() {
        viewModelScope.launch {
            paymentRepository.getAllPayments().collect {
                _payments.value = it
            }
        }
    }

    fun addPayment(
        tenantId: String,
        roomId: String,
        bulanTahun: String,
        jumlahBayar: Int,
        statusPembayaran: String,
        denda: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val payment = Payment(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                roomId = roomId,
                bulanTahun = bulanTahun,
                jumlahBayar = jumlahBayar,
                tanggalBayar = System.currentTimeMillis(),
                statusPembayaran = statusPembayaran,
                denda = denda
            )
            paymentRepository.insertPayment(payment)
            _isLoading.value = false
        }
    }

    fun updatePayment(payment: Payment) {
        viewModelScope.launch {
            _isLoading.value = true
            paymentRepository.updatePayment(payment)
            _isLoading.value = false
        }
    }

    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            _isLoading.value = true
            paymentRepository.deletePayment(payment)
            _isLoading.value = false
        }
    }

    fun syncWithRemote() {
        viewModelScope.launch {
            _isLoading.value = true
            paymentRepository.syncWithRemote()
            _isLoading.value = false
        }
    }
}