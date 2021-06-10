package xyz.nfcv.telephone_directory

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import xyz.nfcv.telephone_directory.data.Account
import xyz.nfcv.telephone_directory.data.CloudApi
import xyz.nfcv.telephone_directory.model.User

class SyncTest {


    @Test
    fun login() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val cloud = CloudApi.retrofit.create(CloudApi::class.java)
        val username = "王程飞"
        val password = "123456"
        cloud.login(username, password)
            .enqueue(CloudApi.defaultCallback { code: Int, user: User? ->
                if (code != 200 || user == null) {
                    Log.d(javaClass.name, "登录失败")
                } else {
                    Account.take(appContext, user)
                    Log.d(javaClass.name, "登录成功")
                }

                Log.d(javaClass.name, "登录结果: ${Account.get(appContext)}")
            })
    }

    @Test
    fun register() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val cloud = CloudApi.retrofit.create(CloudApi::class.java)
        val username = "wcf"
        val password = "123456"
        cloud.register(username, password)
            .enqueue(CloudApi.defaultCallback { code: Int, any: Any? ->
                if (code != 200) {
                    Log.d(javaClass.name, "注册失败")
                } else {
                    Log.d(javaClass.name, "注册成功")
                }

                Log.d(javaClass.name, "注册结果: ${Account.get(appContext)}")
            })
    }
}