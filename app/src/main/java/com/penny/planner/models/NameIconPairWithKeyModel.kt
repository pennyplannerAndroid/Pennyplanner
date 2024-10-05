package com.penny.planner.models

import com.penny.planner.helpers.Utils

data class NameIconPairWithKeyModel(
    var name: String = "",
    var searchKey: String = name.lowercase(),
    var icon: String = Utils.DEFAULT_ICON
)
