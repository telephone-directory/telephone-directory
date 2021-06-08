package xyz.nfcv.telephone_directory

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import xyz.nfcv.telephone_directory.data.Account
import xyz.nfcv.telephone_directory.data.Cloud
import xyz.nfcv.telephone_directory.data.Cloud.Record
import xyz.nfcv.telephone_directory.data.CloudApi
import xyz.nfcv.telephone_directory.data.CloudApi.Companion.defaultCallback
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.telephone_directory.model.User

class CloudService : Service() {

    private val cloud: CloudApi by lazy { CloudApi.retrofit.create(CloudApi::class.java) }
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) { true } }

    inner class CloudBinder : Binder() {
        val service: CloudService = this@CloudService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(javaClass.name, "onBind")
        return CloudBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(javaClass.name, "onUnbind")
        return super.onUnbind(intent)
    }

    fun sync(finish: () -> Unit) {
        val user = Account.get(this)
        if (user != null) {
            cloud.diff(user.userId, user.token, Person.allWithStatus(this))
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
