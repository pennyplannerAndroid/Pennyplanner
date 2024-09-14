package com.penny.planner.ui.screens.mainpage

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ProfileScreen() {
    Text(modifier = Modifier.fillMaxWidth(),
        text = "Profile"
    )
}

@Preview
@Composable
fun PreviewProfileScreen() {
//    ProfileScreen(viewModel = MainActivityViewModel(UserAndExpenseRepositoryImpl(FirebaseAuth.getInstance())))
}