package com.penny.planner.models

import android.net.Uri

data class GroupModel(
    val name: String? = "",
    val id: String? = "",
    val members: List<String>? = listOf(),
    val profileUrl: String? = "",
    val path: Uri? = null
)