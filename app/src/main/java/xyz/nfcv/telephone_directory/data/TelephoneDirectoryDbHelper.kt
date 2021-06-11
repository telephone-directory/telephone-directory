package xyz.nfcv.telephone_directory.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import xyz.nfcv.telephone_directory.data.TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson

class TelephoneDirectoryDbHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    object TelephoneDirectory {
        const val LOCAL_DELETE = -1
        const val SYNCED = 1 shl 3
        const val LOCAL_MODIFY = 1 shl 2
        const val LOCAL_INSERT = 1 shl 1

        object TPerson : BaseColumns {
            const val TABLE_NAME = "telephone_directory"
            const val COLUMN_NAME_NAME = "name"
            const val COLUMN_NAME_TELEPHONE = "telephone"
            const val COLUMN_NAME_EMAIL = "email"
            const val COLUMN_NAME_WORK_ADDRESS = "work_address"
            const val COLUMN_NAME_HOME_ADDRESS = "home_address"
            const val COLUMN_NAME_LIKE = "star"
            const val COLUMN_NAME_STATUS = "status"
            const val COLUMN_NAME_TIME = "time"
        }
    }

    companion object {
        private const val DATABASE_NAME = "TelephoneDirectory.db"
        private const val DATABASE_VERSION = 1

        var helper: TelephoneDirectoryDbHelper? = null

        @Synchronized
        fun getHelper(context: Context): TelephoneDirectoryDbHelper {
            val helper: TelephoneDirectoryDbHelper =
                this.helper ?: TelephoneDirectoryDbHelper(context)
            this.helper = helper
            return helper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table ${TPerson.TABLE_NAME}(${BaseColumns._ID} varchar(36) not null primary key, ${TPerson.COLUMN_NAME_NAME} varchar(64) not null, ${TPerson.COLUMN_NAME_TELEPHONE} varchar(20) not null, ${TPerson.COLUMN_NAME_EMAIL} varchar(20), ${TPerson.COLUMN_NAME_WORK_ADDRESS} varchar(64), ${TPerson.COLUMN_NAME_HOME_ADDRESS} varchar(64), ${TPerson.COLUMN_NAME_LIKE} integer not null, ${TPerson.COLUMN_NAME_STATUS} integer not null, ${TPerson.COLUMN_NAME_TIME} integer not null)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}