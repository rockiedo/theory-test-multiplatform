package com.rdev.tt.core_model

data class Suite(
    val id: Long,
    val name: String,
    val categories: List<@Category String>
)