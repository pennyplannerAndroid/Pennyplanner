package com.penny.planner.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.Utils
import com.penny.planner.ui.theme.PennyPlannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (navToOnboardingIfNeeded()) {
            finish()
            return
        }
        enableEdgeToEdge()
        setContent {
            PennyPlannerTheme {
                Text(text = "Hello")
            }
        }
    }

    private fun navToOnboardingIfNeeded(): Boolean {
        val intent = Intent(this, OnboardingActivity::class.java)
        if (FirebaseAuth.getInstance().currentUser == null)
            intent.putExtra(Utils.NAVIGATION_DESTINATION, Utils.TUTORIAL)
        else if (!FirebaseAuth.getInstance().currentUser?.isEmailVerified!!)
            intent.putExtra(Utils.NAVIGATION_DESTINATION, Utils.EMAIL_VERIFICATION)
        else if (FirebaseAuth.getInstance().currentUser?.displayName == null || FirebaseAuth.getInstance().currentUser?.displayName!!.isEmpty()) {
            intent.putExtra(Utils.NAVIGATION_DESTINATION, Utils.UPDATE_PROFILE)
        } else {
            return false
        }
        startActivity(Intent(this, OnboardingActivity::class.java))
        return true
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PennyPlannerTheme {
    }
}