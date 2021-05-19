package xyz.nfcv.telephone_directory

import android.util.Log
import org.junit.Test
import xyz.nfcv.telephone_directory.model.Person

class PersonNameTest {
    @Test
    fun testName(){
        val people = arrayListOf<Person>(
            Person("1", "王程飞", ""),
            Person("2", "王程灵", ""),
            Person("2", "胡皓睿", ""),
            Person("2", "陈昊天", ""),
            Person("2", "@%¥#%", ""),
            Person("2", "XXX", ""),
            Person("2", "ABC", ""),
            Person("2", "王AQZ", ""),
            Person("2", "12345", ""),
            Person("2", "test", ""),
            Person("2", "", ""),
        )

        people.forEach {
            Log.d(javaClass.name, "${it.name} -> ${it.first}, ${it.last}")
        }
    }
}