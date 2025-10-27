package com.arrive.terminal.core.ui.view.recyclerview.decorator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpaceConfig(
    val topSpace: Int = 0,
    val bottomSpace: Int = 0,
    val leftSpace: Int = 0,
    val rightSpace: Int = 0
) : Parcelable {

    constructor(horizontal: Int = 0, vertical: Int = 0) : this(
        leftSpace = horizontal,
        rightSpace = horizontal,
        topSpace = vertical,
        bottomSpace = vertical
    )

    companion object {

        val EMPTY get() = SpaceConfig(0, 0)
    }
}