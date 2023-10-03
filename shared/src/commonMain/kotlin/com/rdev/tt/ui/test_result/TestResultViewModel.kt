package com.rdev.tt.ui.test_result

import com.rdev.tt.core_model.Category
import com.rdev.tt.data.AppRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel

class TestResultViewModel(
    private val appRepo: AppRepository
) : ViewModel() {
    fun getImageFilePath(name: String?, category: @Category String): String? {
        if (name.isNullOrEmpty()) return null
        return "${appRepo.getImageDir(category)}/$name.png"
    }
}