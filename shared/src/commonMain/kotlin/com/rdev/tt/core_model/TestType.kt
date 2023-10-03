package com.rdev.tt.core_model

@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class Category {
    companion object {
        const val BTT = "btt"
        const val FTT = "ftt"
    }
}