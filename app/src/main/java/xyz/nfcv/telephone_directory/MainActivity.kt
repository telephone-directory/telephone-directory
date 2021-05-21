package xyz.nfcv.telephone_directory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.PeopleGroup
import xyz.nfcv.telephone_directory.adapter.SidebarAdapter
import xyz.nfcv.telephone_directory.databinding.ActivityMainBinding
import xyz.nfcv.widget.Header
import xyz.nfcv.telephone_directory.model.Person
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactorListAdapter: ContactorListAdapter
    private lateinit var sidebarAdapter: SidebarAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        contactorListAdapter = ContactorListAdapter(this, binding.contactorList)

        //region test
        val data = TreeSet<PeopleGroup> { o1, o2 -> o1 - o2 }

        data.add(PeopleGroup(Header.OTHER, listOf(Person("1", "$%^$", "", "", "", ""))))
        data.add(PeopleGroup(Header.LIKE, listOf(Person("1", "胡皓睿", "", "", "", ""))))
        data.add(
            PeopleGroup(
                Header.W, listOf(
                    Person("1", "王程飞", "", "", "", ""),
                    Person("1", "王ABCDFEFD", "", "", "", "")
                )
            )
        )

        data.add(
            PeopleGroup(
                Header.A, listOf(
                    Person("1", "王程飞", "", "", "", ""),
                    Person("1", "王ABCDFEFD", "", "", "", "")
                )
            )
        )

        data.add(
            PeopleGroup(
                Header.Z, listOf(
                    Person("1", "王程飞", "", "", "", ""),
                    Person("1", "王ABCDFEFD", "", "", "", "")
                )
            )
        )

        data.add(
            PeopleGroup(
                Header.B, listOf(
                    Person("1", "王程飞", "", "", "", ""),
                    Person("1", "王ABCDFEFD", "", "", "", "")
                )
            )
        )

        data.add(
            PeopleGroup(
                Header.T, listOf(
                    Person("1", "王程飞", "", "", "", ""),
                    Person("1", "王ABC", "", "", "", "")
                )
            )
        )

        contactorListAdapter.update(data)
        //endregion

        sidebarAdapter = SidebarAdapter(binding.contactSidebar) { position: Int, header: Header ->
            Log.d(javaClass.name, "sidebar: pos@$position, item@${header.value}")
        }

        binding.addContactor.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                MotionEvent.ACTION_UP -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE)
            }
            return@setOnTouchListener false
        }

        binding.scanQrCode.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                MotionEvent.ACTION_UP -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE)
            }
            return@setOnTouchListener false
        }

        binding.addContactor.setOnClickListener {
            startActivity(Intent(this, EditContactorActivity::class.java))

            binding.fabAddContactor.collapse()
        }

        binding.scanQrCode.setOnClickListener {
            binding.fabAddContactor.collapse()
        }
    }
}