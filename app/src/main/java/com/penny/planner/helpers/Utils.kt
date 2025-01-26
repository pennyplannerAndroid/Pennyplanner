package com.penny.planner.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        const val ALL_SET_SCREEN = "all_set_screen"

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
        const val GROUP_NOT_FOUND = "Group not found!"
        const val GROUP_NOT_OPEN = "This group has reached its limit set by the admin!"
        const val ALREADY_A_MEMBER = "You are already a member or have requested to join this group!"
        const val NETWORK_NOT_AVAILABLE = "Network not available!"

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

        //search
        const val FETCHING_GROUP = "Fetching group..."
        const val PLEASE_WAIT = "Please wait..."

        //values
        const val PRICE_LIMIT = 7
        const val NAME_LIMIT = 20
        const val ADMIN_VALUE = 1
        const val NON_ADMIN_VALUE = 0
        const val HOME_PAGE_EXPENSE_DISPLAY_COUNT = 10

        // firebase paths
        const val USERS = "Users"
        const val USER_INFO = "UserInfo"
        const val GROUP_INFO = "GroupInfo"
        const val BUDGET_INFO = "BudgetInfo"
        const val USER_IMAGE = "UserImage"
        const val GROUP_IMAGE = "GroupImage"
        const val GROUPS = "Groups"
        const val PENDING = "Pending"
        const val JOINED = "Joined"
        const val JOIN = "join"
        const val GENERAL_DATA = "GeneralData"
        const val EMOJI_FILE_ID = "emojiFileID"
        const val PROFILE_URL = "profileImage"
        const val BUDGET_DETAILS = "BudgetDetails"
        const val USER_EXPENSES = "UserExpenses"
        const val GROUP_EXPENSES = "GroupExpenses"
        const val EXPENSES = "Expenses"
        const val APPROVALS = "Approvals"

        //db name
        const val PENNY_DATABASE = "penny_database"

        //tables
        const val CATEGORY_TABLE = "category_table"
        const val SUB_CATEGORY_TABLE = "subcategory_table"
        const val EXPENSE_TABLE = "expense_table"
        const val BUDGET_TABLE = "budget_table"
        const val GROUP_TABLE = "group_table"
        const val FRIEND_TABLE = "friend_table"
        const val MONTHLY_EXPENSE_TABLE = "monthly_expense_table"

        //urls
        const val BASE_URL = "https://pennyplanner.shop/"
        const val JOIN_GROUP_QUERY = "join?groupId="

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

        fun getCurrentTimeStamp(): Timestamp {
            val calendar = Calendar.getInstance()
            val savedDate = calendar.time
            return Timestamp(savedDate)
        }

        fun getCurrentMonthYear(): String {
            val dateFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            return dateFormatter.format(Calendar.getInstance().time)
        }

        fun getProgress(budget: Double, expense: Double) : Double  {
            if (expense == 0.0) return 0.0
            return expense / budget
        }

        fun lengthHint(value: Int, limit: Int) = limit - value

        fun getSafeToSpendValue(budgetValue: Double, safeToSpendLimit: Int, expenseSoFar: Double) =
            (((budgetValue * safeToSpendLimit) / 100) - expenseSoFar).coerceAtLeast(0.0)

        fun moreThanADay(lastUpdate: Long): Boolean {
            return System.currentTimeMillis() - lastUpdate > java.util.concurrent.TimeUnit.DAYS.toMillis(1)
        }

        fun isNetworkAvailable(context: Context?): Boolean {
            if (context == null) return false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            return true
                        }
                    }
                }
            } else {
                return true
            }
            return false
        }
    }
}