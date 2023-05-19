package com.example.demogetapppermission.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class JunkClean(
    var id: Long = 0L,
    var name: String? = null,
    var size: Long = 0,
    var isChecked: Boolean = false,
    var packageName: String = "",
    var isRoot: Boolean = false,
    var isExpand: Boolean = false,
    var totalItem: Int? = null,
    var isIgnore: Boolean = false,
    var timeCleaned: Long = 0L
) : Serializable