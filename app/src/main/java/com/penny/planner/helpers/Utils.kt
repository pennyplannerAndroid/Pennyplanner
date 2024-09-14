package com.penny.planner.helpers

import android.graphics.Bitmap
import android.graphics.Picture
import java.io.ByteArrayOutputStream

class Utils {
    companion object Const {

        // Onboarding Navigation
        const val TUTORIAL = "tutorial"
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val EMAIL_VERIFICATION = "email_Verification"
        const val FORGOT_PASSWORD = "forgot_Password"
        const val UPDATE_PROFILE = "update_Profile"
        const val EMAIL_SENT = "email_sent"

        // Home Navigation
        const val HOME = "Home"
        const val ADD = "Add"
        const val BUDGET = "Budget"
        const val PROFILE = "Profile"

        // Key
        const val EMAIL = "email"
        const val DEFAULT_EMAIL_STRING = "@"
        const val NAVIGATION_DESTINATION = "navigation_destination"
        const val CLICK_TAG = "click_tag"
        const val USER = "User"

        //Errors
        const val FAILED = "Operation Failed"
        const val USER_NOT_FOUND = "User not found"

        // regex
        private const val SPECIAL_CHARACTERS = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        const val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$SPECIAL_CHARACTERS])(?=\\S+$).{8,20}$"

        const val PROVIDER = "com.penny.planner.provider"

        // firebase paths
        const val USERS = "Users"
        const val USER_INFO = "UserInfo"
        const val USER_IMAGE = "UserImage"
        const val GROUPS = "Groups"
        const val PENDING = "Pending"
        const val JOINED = "JOINED"

        //tables
        const val CATEGORY_TABLE = "category_table"
        const val SUB_CATEGORY_TABLE = "subcategory_table"
        const val EXPENSE_TABLE = "expense_table"

        fun formatEmailForFirebase(email: String): String {
            return email.replace('.', ',')
        }

        fun createBitmapFromPicture(picture: Picture): ByteArray {
            val bitmap = Bitmap.createBitmap(
                picture.width,
                picture.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            canvas.drawPicture(picture)
            val byteArrayStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream)
            return byteArrayStream.toByteArray()
        }
    }
}