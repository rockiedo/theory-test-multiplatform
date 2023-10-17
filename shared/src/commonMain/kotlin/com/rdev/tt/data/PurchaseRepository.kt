package com.rdev.tt.data

import com.rdev.tt.core_model.Category

interface PurchaseRepository {
    suspend fun didPurchase(category: @Category String): Boolean
    suspend fun purchase(category: @Category String)
    suspend fun purchaseBundle()
}