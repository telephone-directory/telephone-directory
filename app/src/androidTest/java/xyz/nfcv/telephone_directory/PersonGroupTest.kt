package xyz.nfcv.telephone_directory

import android.util.Log
import org.junit.Test
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.PeopleGroup
import xyz.nfcv.widget.Header
import xyz.nfcv.telephone_directory.model.PeopleGroupData
import xyz.nfcv.telephone_directory.model.Person
import java.util.*

class PersonGroupTest {
    @Test
    fun testGroup() {
        val data = TreeSet<PeopleGroup> { o1, o2 -> o1 - o2 }

        data.add(
            PeopleGroup(
                Header.OTHER,
                listOf(Person("1", "$%^$", "", "", "", ""))
            )
        )
        data.add(
            PeopleGroup(
                Header.LIKE,
                listOf(Person("1", "胡皓睿", "", "", "", ""))
            )
        )
        data.add(
            PeopleGroup(
                Header.W, listOf(
                    Person("1", "王程飞", "", "", "", ""),
                    Person("1", "王XX", "", "", "", ""),
                    Person("1", "王ABC", "", "", "", "")
                )
            )
        )

        PeopleGroupData(data.toList()).apply {
            Log.d(javaClass.name, size.toString())
            for (i in 0 until size) {
                Log.d(javaClass.name, "${this[i]} ${if (first(i)) "<" else " "} ${if (last(i)) ">" else " "}")
            }
        }
    }
}