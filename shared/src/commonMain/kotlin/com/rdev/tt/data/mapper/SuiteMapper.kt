package com.rdev.tt.data.mapper

import com.rdev.tt.core_model.Suite
import com.rdev.tt.data.database.SuiteEntity

fun SuiteEntity.toModel() = Suite(
    id = this.id,
    name = this.name,
    categories = this.categories
)