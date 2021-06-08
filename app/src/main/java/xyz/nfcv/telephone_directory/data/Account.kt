package xyz.nfcv.telephone_directory.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import xyz.nfcv.telephone_directory.model.User

object Account {
    private const val PREFERENCE_NAME = "account"
    private const val COLUMN_ID = "id"
    private const val COLUMN_TOKEN = "token"

    fun take(context: Context, user: User) {
        val preferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        preferences.edit {
            putString(COLUMN_ID, user.userId)
            putString(COLUMN_TOKEN, user.token)
        }
    }

    fun get(context: Context): User? {
        val preferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val userId = preferences.getString(COLUMN_ID, null)
        val token = preferences.getString(COLUMN_TOKEN, null)

        if (userId.isNullOrBlank() || token.isNullOrBlank()) {
            return null
        }
        return User(userId, token)
    }

    fun remove(context: Context) {
        val preferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        preferences.edit {
            putString(COLUMN_ID, null)
            putString(COLUMN_TOKEN, null)
        }
    }
}