package com.penny.planner

class Utils {
    companion object Navigation {

        // Onboarding Navigation
        const val TUTORIAL = "tutorial"
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val EMAIL_VERIFICATION = "email_Verification"
        const val FORGOT_PASSWORD = "forgot_Password"
        const val UPDATE_PROFILE = "update_Profile"
        const val EMAIL_SENT = "email_sent"

        // Key
        const val EMAIL = "email"
        const val DEFAULT_EMAIL_STRING = "@"
        const val NAVIGATION_DESTINATION = "navigation_destination"
        const val CLICK_TAG = "click_tag"

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

    }
}