package com.example.kostkita.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kostkita.data.local.dao.PaymentDao
import com.example.kostkita.data.local.dao.RoomDao
import com.example.kostkita.data.local.dao.TenantDao
import com.example.kostkita.data.local.entity.PaymentEntity
import com.example.kostkita.data.local.entity.RoomEntity
import com.example.kostkita.data.local.entity.TenantEntity

@Database(
    entities = [TenantEntity::class, RoomEntity::class, PaymentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KostKitaDatabase : RoomDatabase() {
    abstract fun tenantDao(): TenantDao
    abstract fun roomDao(): RoomDao
    abstract fun paymentDao(): PaymentDao
}