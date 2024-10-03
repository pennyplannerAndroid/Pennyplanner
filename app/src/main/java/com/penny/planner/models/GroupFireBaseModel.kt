package com.penny.planner.models

data class GroupFireBaseModel(
    val groupId: String = "",
    val name: String = "",
    var members: List<String> = listOf(),
    var profileUrl: String? = "",
    var creatorId: String? = "",
)