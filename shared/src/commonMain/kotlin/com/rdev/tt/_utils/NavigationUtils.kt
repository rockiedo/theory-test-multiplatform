package com.rdev.tt._utils

import cafe.adriel.voyager.navigator.Navigator

fun Navigator.safePop() {
    if (canPop) pop()
}