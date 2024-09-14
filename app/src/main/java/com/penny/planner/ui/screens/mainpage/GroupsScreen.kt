package com.penny.planner.ui.screens.mainpage

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.penny.planner.viewmodels.MainActivityViewModel

@Composable
fun GroupsScreen(
    viewModel: MainActivityViewModel?
) {
    Text(modifier = Modifier.fillMaxWidth(),
        text = "Groups"
    )
}