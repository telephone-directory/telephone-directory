package xyz.nfcv.telephone_directory

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService
import xyz.nfcv.telephone_directory.data.Account
import xyz.nfcv.telephone_directory.data.Cloud
import xyz.nfcv.telephone_directory.data.Cloud.Record
import xyz.nfcv.telephone_directory.data.CloudApi
import xyz.nfcv.telephone_directory.data.CloudApi.Companion.defaultCallback
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.telephone_directory.model.User
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CloudService : JobIntentService() {

    private val cloud: CloudApi by lazy { CloudApi.retrofit.create(CloudApi::class.java) }
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) { true } }

    override fun onHandleWork(intent: Intent) {
        Log.d(javaClass.name, "JobIntentService Start @${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(Date())} Thread: ${Thread.currentThread().id}")
        val user = Account.get(this)
        if (user != null) {
            try {
                val locals = Person.allWithStatus(this)
                Log.d(javaClass.name, "LOCAL")
                locals.forEach {
                    Log.d(javaClass.name, it.toString())
                }
                val call = cloud.sync(user.userId, user.token, locals)
                val response = call.execute()
                val data = response.body()
                if (response.isSuccessful && response.code() == 200) {
                    if (data != null && data.code == 200) {
                        Log.d(javaClass.name, "REMOTE")
                        data.data?.forEach {
                            Log.d(javaClass.name, it.toString())
                        }
                        data.data?.let { records ->
                            Cloud.invalidate(this, records)
                            handler.post {
                                Toast.makeText(this@CloudService, "同步成功", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.d(javaClass.name, "登录过期")
                        handler.post {
                            Toast.makeText(this@CloudService, "登录过期", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d(javaClass.name, "同步错误")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            Log.d(javaClass.name, "未登录")
        }
        Log.d(javaClass.name, "JobIntentService Finish@${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(Date())} Thread: ${Thread.currentThread().id}")
    }

    fun sync(finish: () -> Unit) {
        val user = Account.get(this)
        if (user != null) {
            cloud.sync(user.userId, user.token, Person.allWithStatus(this))
                .enqueue(defaultCallback { code: Int, data: List<Record>? ->
                    if (code != 200 || data == null) {
                        Log.d(javaClass.name, "验证错误")
                    } else {
                        Cloud.invalidate(this@CloudService, data)
                        handler.post { finish() }
                    }
                })
        } else {
            Log.d(javaClass.name, "未登录")
        }
    }

    fun login(username: String, password: String, callback: (Int, String) -> Unit) {
        cloud.login(username, password)
            .enqueue(defaultCallback { code: Int, user: User? ->
                if (code != 200 || user == null) {
                    handler.post { callback(code, "登录失败") }
                } else {
                    Account.take(this, user)
                    handler.post { callback(code, "登录成功") }
                }
            })
    }

    fun register(username: String, password: String, callback: (Int, String) -> Unit) {
        cloud.register(username, password)
            .enqueue(defaultCallback { code: Int, data: Any? ->
                if (code != 200 || data == null) {
                    handler.post { callback(code, "注册失败") }
                } else {
                    handler.post { callback(code, "注册成功") }
                }
            })
    }

    fun validate(callback: (Int, String) -> Unit) {
        val user = Account.get(this)
        if (user != null) {
            cloud.validate(user.userId, user.token)
                .enqueue(defaultCallback { code: Int, data: Any? ->
                    if (code != 200 || data == null) {
                        handler.post { callback(code, "验证失败") }
                    } else {
                        handler.post { callback(code, "验证成功") }
                    }
                })
        }
    }

    fun logout() {
        Account.remove(this)
    }
}
