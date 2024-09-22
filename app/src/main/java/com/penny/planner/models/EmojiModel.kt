package com.penny.planner.models

import com.google.gson.annotations.SerializedName

data class EmojiModel(
    @SerializedName("emoji") val emoji: Map<String, String>,
    @SerializedName("recommended_categories") val recommendedCategories: Map<String, List<String>>
)