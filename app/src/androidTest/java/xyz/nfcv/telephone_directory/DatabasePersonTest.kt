package xyz.nfcv.telephone_directory

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import xyz.nfcv.telephone_directory.model.Person

@RunWith(AndroidJUnit4::class)
class DatabasePersonTest {
    @Test
    fun getAll() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val persons = Person.all(appContext)
        persons.forEach(::println)
    }

    @Test
    fun insert() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Person.insert(appContext, Person(null, "xxx", "17xxxxxxxxxx", "xxx@zjut.edu.cn", "浙江省杭州市西湖区留下街道留和路288号浙江工业大学屏峰校区", "浙江省XX市XX县XX镇XX村", 0))
    }

    @Test
    fun like() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val personsLikeName = Person.likeName(appContext, "xx")
        val personsLikeAddress = Person.likeAddress(appContext, "XX村")
        personsLikeName.forEach(::println)
        personsLikeAddress.forEach(::println)
    }

    @Test
    fun update() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val person = Person.all(appContext)[0]
        person.email = "XXXXXX@qq.com"
        person.telephone = "123456778901"
        person.like = 1
        val result = Person.update(appContext, person)
    }
}