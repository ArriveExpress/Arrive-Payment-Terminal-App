package com.arrive.terminal.presentation.adapter;

import androidx.annotation.DrawableRes
import com.arrive.terminal.core.ui.model.StringValue

interface RVItem

data class RideItem(
    val id: String,
    val name: StringValue,
    val price: String?,
    @DrawableRes val icon: Int?,
) : RVItem

data class FlaggedTripItem(
    val id: String,
    val name: StringValue,
) : RVItem