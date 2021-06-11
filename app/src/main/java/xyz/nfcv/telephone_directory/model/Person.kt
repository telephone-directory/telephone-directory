package xyz.nfcv.telephone_directory.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import xyz.nfcv.telephone_directory.data.TelephoneDirectoryDbHelper
import xyz.nfcv.telephone_directory.data.TelephoneDirectoryDbHelper.TelephoneDirectory
import xyz.nfcv.telephone_directory.data.TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson
import xyz.nfcv.widget.Header
import java.util.*
import java.util.regex.Pattern


data class Person(
    val id: String?,
    var name: String,
    var telephone: String,
    var email: String? = null,
    var workAddress: String? = null,
    var homeAddress: String? = null,
    var like: Int = 0,
    var status: Int = 0,
    var time: Long = 0L
) : Comparable<Person> {

    companion object {
        fun insert(context: Context, vararg persons: Person) {
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    for (person in persons) {
                        person.status = TelephoneDirectory.LOCAL_INSERT
                        person.time = System.currentTimeMillis()
                        val contentValues = ContentValues()
                        contentValues.put(BaseColumns._ID, UUID.randomUUID().toString())
                        contentValues.put(TPerson.COLUMN_NAME_NAME, person.name)
                        contentValues.put(TPerson.COLUMN_NAME_TELEPHONE, person.telephone)
                        contentValues.put(TPerson.COLUMN_NAME_EMAIL, person.email)
                        contentValues.put(TPerson.COLUMN_NAME_WORK_ADDRESS, person.workAddress)
                        contentValues.put(TPerson.COLUMN_NAME_HOME_ADDRESS, person.homeAddress)
                        contentValues.put(TPerson.COLUMN_NAME_LIKE, person.like)
                        contentValues.put(TPerson.COLUMN_NAME_STATUS, person.status)
                        contentValues.put(TPerson.COLUMN_NAME_TIME, person.time)
                        db.insert(TPerson.TABLE_NAME, null, contentValues)
                    }
                }
            }
        }

        fun insertCloud(context: Context, vararg persons: Person) {
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    for (person in persons) {
                        if (person.id == null) {
                            continue
                        }
                        person.status = TelephoneDirectory.SYNCED
                        val contentValues = ContentValues()
                        contentValues.put(BaseColumns._ID, person.id)
                        contentValues.put(TPerson.COLUMN_NAME_NAME, person.name)
                        contentValues.put(TPerson.COLUMN_NAME_TELEPHONE, person.telephone)
                        contentValues.put(TPerson.COLUMN_NAME_EMAIL, person.email)
                        contentValues.put(TPerson.COLUMN_NAME_WORK_ADDRESS, person.workAddress)
                        contentValues.put(TPerson.COLUMN_NAME_HOME_ADDRESS, person.homeAddress)
                        contentValues.put(TPerson.COLUMN_NAME_LIKE, person.like)
                        contentValues.put(TPerson.COLUMN_NAME_STATUS, person.status)
                        contentValues.put(TPerson.COLUMN_NAME_TIME, person.time)
                        db.insert(TPerson.TABLE_NAME, null, contentValues)
                    }
                }
            }
        }

        fun delete(context: Context, person: Person): Int {
            if (person.id == null) {
                return -1
            }

            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    person.status = TelephoneDirectory.LOCAL_DELETE
                    person.time = System.currentTimeMillis()
                    val contentValues = ContentValues()
                    contentValues.put(TPerson.COLUMN_NAME_STATUS, person.status)
                    contentValues.put(TPerson.COLUMN_NAME_TIME, person.time)
                    return db.update(
                        TPerson.TABLE_NAME,
                        contentValues,
                        "${BaseColumns._ID} = ?",
                        arrayOf(person.id)
                    )
                }
            }
        }

        fun deleteCloud(context: Context, person: Person): Int {
            if (person.id == null) {
                return -1
            }

            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    return db.delete(
                        TPerson.TABLE_NAME,
                        "${BaseColumns._ID} = ?",
                        arrayOf(person.id)
                    )
                }
            }
        }

        fun update(context: Context, person: Person): Int {
            if (person.id == null) {
                return -1
            }

            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    person.status =
                        TelephoneDirectory.LOCAL_MODIFY or (person.status and TelephoneDirectory.LOCAL_INSERT)
                    person.time = System.currentTimeMillis()
                    val contentValues = ContentValues()
                    contentValues.put(TPerson.COLUMN_NAME_NAME, person.name)
                    contentValues.put(TPerson.COLUMN_NAME_TELEPHONE, person.telephone)
                    contentValues.put(TPerson.COLUMN_NAME_EMAIL, person.email)
                    contentValues.put(TPerson.COLUMN_NAME_WORK_ADDRESS, person.workAddress)
                    contentValues.put(TPerson.COLUMN_NAME_HOME_ADDRESS, person.homeAddress)
                    contentValues.put(TPerson.COLUMN_NAME_LIKE, person.like)
                    contentValues.put(TPerson.COLUMN_NAME_STATUS, person.status)
                    contentValues.put(TPerson.COLUMN_NAME_TIME, person.time)
                    return db.update(
                        TPerson.TABLE_NAME,
                        contentValues,
                        "${BaseColumns._ID} = ?",
                        arrayOf(person.id)
                    )
                }
            }
        }

        fun updateCloud(context: Context, person: Person): Int {
            if (person.id == null) {
                return -1
            }

            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    person.status = TelephoneDirectory.SYNCED
//                    person.time = System.currentTimeMillis()
                    val contentValues = ContentValues()
                    contentValues.put(TPerson.COLUMN_NAME_NAME, person.name)
                    contentValues.put(TPerson.COLUMN_NAME_TELEPHONE, person.telephone)
                    contentValues.put(TPerson.COLUMN_NAME_EMAIL, person.email)
                    contentValues.put(TPerson.COLUMN_NAME_WORK_ADDRESS, person.workAddress)
                    contentValues.put(TPerson.COLUMN_NAME_HOME_ADDRESS, person.homeAddress)
                    contentValues.put(TPerson.COLUMN_NAME_LIKE, person.like)
                    contentValues.put(TPerson.COLUMN_NAME_STATUS, person.status)
                    contentValues.put(TPerson.COLUMN_NAME_TIME, person.time)
                    return db.update(
                        TPerson.TABLE_NAME,
                        contentValues,
                        "${BaseColumns._ID} = ?",
                        arrayOf(person.id)
                    )
                }
            }
        }

        fun synced(context: Context, person: Person): Int {
            if (person.id == null) {
                return -1
            }

            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    person.status = TelephoneDirectory.SYNCED
//                    person.time = System.currentTimeMillis()
                    val contentValues = ContentValues()
                    contentValues.put(TPerson.COLUMN_NAME_STATUS, person.status)
                    contentValues.put(TPerson.COLUMN_NAME_TIME, person.time)
                    return db.update(
                        TPerson.TABLE_NAME,
                        contentValues,
                        "${BaseColumns._ID} = ?",
                        arrayOf(person.id)
                    )
                }
            }
        }

        fun all(context: Context): List<Person> {
            val persons = arrayListOf<Person>()
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.readableDatabase.use { db: SQLiteDatabase ->
                    val cursor = db.query(
                        TPerson.TABLE_NAME,
                        arrayOf(
                            BaseColumns._ID,
                            TPerson.COLUMN_NAME_NAME,
                            TPerson.COLUMN_NAME_TELEPHONE,
                            TPerson.COLUMN_NAME_EMAIL,
                            TPerson.COLUMN_NAME_WORK_ADDRESS,
                            TPerson.COLUMN_NAME_HOME_ADDRESS,
                            TPerson.COLUMN_NAME_LIKE,
                            TPerson.COLUMN_NAME_STATUS,
                            TPerson.COLUMN_NAME_TIME
                        ),
                        "${TPerson.COLUMN_NAME_STATUS} >= 0",
                        null,
                        null,
                        null,
                        null
                    )
                    cursor.use {
                        with(cursor) {
                            while (cursor.moveToNext()) {
                                val person = Person(
                                    getString(getColumnIndexOrThrow(BaseColumns._ID)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_NAME)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TELEPHONE)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_EMAIL)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_WORK_ADDRESS)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_HOME_ADDRESS)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_LIKE)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_STATUS)),
                                    getLong(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TIME))
                                )
                                persons.add(person)
                            }
                        }
                    }
                }
            }

            return persons
        }

        fun allWithStatus(context: Context): List<Person> {
            val persons = arrayListOf<Person>()
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.readableDatabase.use { db: SQLiteDatabase ->
                    val cursor = db.query(
                        TPerson.TABLE_NAME,
                        arrayOf(
                            BaseColumns._ID,
                            TPerson.COLUMN_NAME_NAME,
                            TPerson.COLUMN_NAME_TELEPHONE,
                            TPerson.COLUMN_NAME_EMAIL,
                            TPerson.COLUMN_NAME_WORK_ADDRESS,
                            TPerson.COLUMN_NAME_HOME_ADDRESS,
                            TPerson.COLUMN_NAME_LIKE,
                            TPerson.COLUMN_NAME_STATUS,
                            TPerson.COLUMN_NAME_TIME
                        ),
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                    cursor.use {
                        with(cursor) {
                            while (cursor.moveToNext()) {
                                val person = Person(
                                    getString(getColumnIndexOrThrow(BaseColumns._ID)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_NAME)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TELEPHONE)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_EMAIL)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_WORK_ADDRESS)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_HOME_ADDRESS)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_LIKE)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_STATUS)),
                                    getLong(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TIME))
                                )
                                persons.add(person)
                            }
                        }
                    }
                }
            }

            return persons
        }

        fun clear(context: Context): Int {
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.writableDatabase.use { db: SQLiteDatabase ->
                    return db.delete(TPerson.TABLE_NAME, null, null)
                }
            }
        }

        fun select(context: Context, value: String): Person? {
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.readableDatabase.use { db: SQLiteDatabase ->
                    val cursor = db.query(
                        TPerson.TABLE_NAME,
                        arrayOf(
                            BaseColumns._ID,
                            TPerson.COLUMN_NAME_NAME,
                            TPerson.COLUMN_NAME_TELEPHONE,
                            TPerson.COLUMN_NAME_EMAIL,
                            TPerson.COLUMN_NAME_WORK_ADDRESS,
                            TPerson.COLUMN_NAME_HOME_ADDRESS,
                            TPerson.COLUMN_NAME_LIKE,
                            TPerson.COLUMN_NAME_STATUS,
                            TPerson.COLUMN_NAME_TIME
                        ),
                        "${TPerson.COLUMN_NAME_STATUS} >= 0 and ${BaseColumns._ID} = ?",
                        arrayOf(value),
                        null,
                        null,
                        null
                    )
                    cursor.use {
                        with(cursor) {
                            while (cursor.moveToNext()) {
                                return Person(
                                    getString(getColumnIndexOrThrow(BaseColumns._ID)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_NAME)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TELEPHONE)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_EMAIL)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_WORK_ADDRESS)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_HOME_ADDRESS)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_LIKE)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_STATUS)),
                                    getLong(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TIME))
                                )
                            }
                        }
                    }
                }
            }
            return null
        }

        fun likeName(context: Context, value: String): ArrayList<Person> {
            val persons = arrayListOf<Person>()
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.readableDatabase.use { db: SQLiteDatabase ->
                    val cursor = db.query(
                        TPerson.TABLE_NAME,
                        arrayOf(
                            BaseColumns._ID,
                            TPerson.COLUMN_NAME_NAME,
                            TPerson.COLUMN_NAME_TELEPHONE,
                            TPerson.COLUMN_NAME_EMAIL,
                            TPerson.COLUMN_NAME_WORK_ADDRESS,
                            TPerson.COLUMN_NAME_HOME_ADDRESS,
                            TPerson.COLUMN_NAME_LIKE,
                            TPerson.COLUMN_NAME_STATUS,
                            TPerson.COLUMN_NAME_TIME
                        ),
                        "${TPerson.COLUMN_NAME_STATUS} >= 0 and ${TPerson.COLUMN_NAME_NAME} like ?",
                        arrayOf("%${value}%"),
                        null,
                        null,
                        null
                    )
                    cursor.use {
                        with(cursor) {
                            while (cursor.moveToNext()) {
                                val person = Person(
                                    getString(getColumnIndexOrThrow(BaseColumns._ID)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_NAME)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TELEPHONE)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_EMAIL)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_WORK_ADDRESS)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_HOME_ADDRESS)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_LIKE)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_STATUS)),
                                    getLong(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TIME))
                                )
                                persons.add(person)
                            }
                        }
                    }
                }
            }

            return persons
        }

        fun likeAddress(context: Context, value: String): ArrayList<Person> {
            val persons = arrayListOf<Person>()
            TelephoneDirectoryDbHelper(context).use { helper: TelephoneDirectoryDbHelper ->
                helper.readableDatabase.use { db: SQLiteDatabase ->
                    val cursor = db.query(
                        TPerson.TABLE_NAME,
                        arrayOf(
                            BaseColumns._ID,
                            TPerson.COLUMN_NAME_NAME,
                            TPerson.COLUMN_NAME_TELEPHONE,
                            TPerson.COLUMN_NAME_EMAIL,
                            TPerson.COLUMN_NAME_WORK_ADDRESS,
                            TPerson.COLUMN_NAME_HOME_ADDRESS,
                            TPerson.COLUMN_NAME_LIKE,

                            ),
                        "${TPerson.COLUMN_NAME_STATUS} >= 0 and ${TPerson.COLUMN_NAME_WORK_ADDRESS} like ? or ${TPerson.COLUMN_NAME_HOME_ADDRESS} like ?",
                        arrayOf("%${value}%", "%${value}%"),
                        null,
                        null,
                        null
                    )
                    cursor.use {
                        with(cursor) {
                            while (cursor.moveToNext()) {
                                val person = Person(
                                    getString(getColumnIndexOrThrow(BaseColumns._ID)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_NAME)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TELEPHONE)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_EMAIL)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_WORK_ADDRESS)),
                                    getString(getColumnIndexOrThrow(TPerson.COLUMN_NAME_HOME_ADDRESS)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_LIKE)),
                                    getInt(getColumnIndexOrThrow(TPerson.COLUMN_NAME_STATUS)),
                                    getLong(getColumnIndexOrThrow(TPerson.COLUMN_NAME_TIME))
                                )
                                persons.add(person)
                            }
                        }
                    }
                }
            }

            return persons
        }
    }

    override fun compareTo(other: Person): Int {
        return this.name.compareTo(other.name)
    }

    val last: String
        get() {
            val chinese = Pattern.compile("[\u4e00-\u9fa5]").matcher(name).find()
            return if (chinese) {
                "${name.trim().uppercase().lastOrNull() ?: "#"}"
            } else {
                val words = name.split(" ").filter { it.isNotBlank() }
                val l = words.mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
                if (l.isBlank()) "#" else l
            }
        }

    val first: Header
        get() {
            if (like != 0) {
                return Header.LIKE
            }
            val pinyin = PinyinHelper.toHanYuPinyinString(
                name.trim().uppercase(),
                HanyuPinyinOutputFormat().apply {
                    caseType = HanyuPinyinCaseType.UPPERCASE
                    toneType = HanyuPinyinToneType.WITHOUT_TONE
                    vCharType = HanyuPinyinVCharType.WITH_V
                },
                " ",
                true
            )
            val c = pinyin?.firstOrNull() ?: '#'
            return Header.values().firstOrNull { it.value == "$c" } ?: Header.OTHER
        }
}