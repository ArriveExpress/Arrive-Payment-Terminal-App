package com.arrive.terminal.core.ui.extensions

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

fun AppCompatActivity.findNavController(@IdRes id: Int): NavController {
    return (supportFragmentManager.findFragmentById(id) as NavHostFragment).navController
}