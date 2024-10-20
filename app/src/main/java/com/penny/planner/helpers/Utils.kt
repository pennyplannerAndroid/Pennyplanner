package com.penny.planner.helpers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Picture
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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
        const val SET_MONTHLY_BUDGET = "set_monthly_budget"

        // Home Navigation
        const val HOME = "Home"
        const val BUDGET = "Budget"
        const val PROFILE = "Profile"

        // Main Page Navigation
        const val MAIN_PAGE = "main_page"
        const val ADD_EXPENSE = "add_expense"
        const val CREATE_GROUP = "create_group"
        const val GROUP_SESSION = "group_session"

        // Key
        const val EMAIL = "email"
        const val DEFAULT_EMAIL_STRING = "@"
        const val NAVIGATION_DESTINATION = "navigation_destination"
        const val CLICK_TAG = "click_tag"
        const val USER = "User"

        //Errors
        const val FAILED = "Operation Failed"
        const val USER_NOT_FOUND = "User not found"
        const val SAME_EMAIL_ERROR = "You can't make group with your own!"
        const val SESSION_EXPIRED_ERROR = "Session expired! Login again."

        // regex
        private const val SPECIAL_CHARACTERS = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        const val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$SPECIAL_CHARACTERS])(?=\\S+$).{8,20}$"

        const val PROVIDER = "com.penny.planner.provider"
        const val RUPEE = "₹"
        const val EMOJI_JSON = "emojiJson"
        const val DEFAULT_ICON = "🏷️"
        const val DEFAULT = "Default"
        const val GROUP_ID = "group_id"
        const val TIME = "time"

        //values
        const val PRICE_LIMIT = 7
        const val ADMIN_VALUE = 1
        const val NON_ADMIN_VALUE = 0

        // firebase paths
        const val USERS = "Users"
        const val USER_INFO = "UserInfo"
        const val GROUP_INFO = "GroupInfo"
        const val BUDGET_INFO = "BudgetInfo"
        const val USER_IMAGE = "UserImage"
        const val GROUPS = "Groups"
        const val PENDING = "Pending"
        const val JOINED = "JOINED"
        const val GENERAL_DATA = "GeneralData"
        const val EMOJI_FILE_ID = "emojiFileID"
        const val PROFILE_URL = "profileUrl"
        const val BUDGET_DETAILS = "BudgetDetails"
        const val USER_EXPENSES = "UserExpenses"
        const val GROUP_EXPENSES = "GroupExpenses"
        const val EXPENSES = "Expenses"

        //db name
        const val PENNY_DATABASE = "penny_database"

        //tables
        const val CATEGORY_TABLE = "category_table"
        const val SUB_CATEGORY_TABLE = "subcategory_table"
        const val EXPENSE_TABLE = "expense_table"
        const val BUDGET_TABLE = "budget_table"
        const val GROUP_TABLE = "group_table"
        const val FRIEND_TABLE = "friend_table"

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

        @SuppressLint("SimpleDateFormat")
        fun convertMillisToTime(date: Date): String {
            return SimpleDateFormat("HH:mm").format(date)
        }

        fun getPaymentTypes() = mapOf(Pair("Cash", "💵"), Pair("UPI", "🆙"), Pair("Card", "💳"))

        fun getDefaultTimestamp(): Timestamp {
            val calendar = Calendar.getInstance()
            calendar.set(2024, Calendar.JANUARY, 23, 5, 30, 0)
            val savedDate = calendar.time
            return Timestamp(savedDate)
        }

        fun lengthHint(value: Int, limit: Int) = limit - value
    }
}