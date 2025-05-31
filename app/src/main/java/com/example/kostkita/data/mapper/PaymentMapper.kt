package com.example.kostkita.data.mapper

import com.example.kostkita.data.local.entity.PaymentEntity
import com.example.kostkita.data.remote.dto.PaymentDto
import com.example.kostkita.domain.model.Payment

fun PaymentEntity.toDomain(): Payment {
    return Payment(
        id = id,
        tenantId = tenantId,
        roomId = roomId,
        bulanTahun = bulanTahun,
        jumlahBayar = jumlahBayar,
        tanggalBayar = tanggalBayar,
        statusPembayaran = statusPembayaran,
        denda = denda
    )
}

fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        id = id,
        tenantId = tenantId,
        roomId = roomId,
        bulanTahun = bulanTahun,
        jumlahBayar = jumlahBayar,
        tanggalBayar = tanggalBayar,
        statusPembayaran = statusPembayaran,
        denda = denda
    )
}

fun PaymentDto.toDomain(): Payment {
    return Payment(
        id = id,
        tenantId = tenant_id,
        roomId = room_id,
        bulanTahun = bulan_tahun,
        jumlahBayar = jumlah_bayar,
        tanggalBayar = tanggal_bayar,
        statusPembayaran = status_pembayaran,
        denda = denda
    )
}

fun Payment.toDto(): PaymentDto {
    return PaymentDto(
        id = id,
        tenant_id = tenantId,
        room_id = roomId,
        bulan_tahun = bulanTahun,
        jumlah_bayar = jumlahBayar,
        tanggal_bayar = tanggalBayar,
        status_pembayaran = statusPembayaran,
        denda = denda
    )
}