package xyz.nfcv.telephone_directory

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.PeopleGroup
import xyz.nfcv.telephone_directory.adapter.SidebarAdapter
import xyz.nfcv.telephone_directory.databinding.ActivityMainBinding
import xyz.nfcv.telephone_directory.model.Header
import xyz.nfcv.telephone_directory.model.Person
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var contactorListAdapter: ContactorListAdapter
    lateinit var sidebarAdapter: SidebarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contactorListAdapter = ContactorListAdapter(this, binding.contactorList)

        val data = TreeSet<PeopleGroup> { o1, o2 -> o1 - o2 }

        data.add(PeopleGroup(Header.OTHER, listOf(Person("1", "$%^$", "", "", "", ""))))
        data.add(PeopleGroup(Header.LIKE, listOf(Person("1", "胡皓睿", "", "", "", ""))))
        data.add(
            PeopleGroup(
                Header.W, listOf(
                    Person("1", "王程飞", "", "", "", ""),
                    Person("1", "王ABC", "", "", "", "")
                )
            )
        )

        contactorListAdapter.update(data)

        sidebarAdapter = SidebarAdapter(binding.contactSidebar) { position: Int, header: Header ->
            Log.d(javaClass.name, "sidebar: pos@$position, item@${header.value}")
        }
    }
}