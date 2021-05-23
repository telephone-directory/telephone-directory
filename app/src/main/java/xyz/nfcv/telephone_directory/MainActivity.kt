package xyz.nfcv.telephone_directory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.PeopleGroup
import xyz.nfcv.telephone_directory.databinding.ActivityMainBinding
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.widget.Header
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactorListAdapter: ContactorListAdapter

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

        binding.contactorSearchBar.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
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

            binding.fabContactorMenu.collapse()
        }

        binding.scanQrCode.setOnClickListener {
            binding.fabContactorMenu.collapse()
        }

        binding.contactSidebar.addListener { _, header ->
            contactorListAdapter.near(header)?.let {
                contactorListAdapter.scroll(it)
            }
        }

        binding.contactorList.setOnScrollListener(object : AbsListView.OnScrollListener {
            var first: Header? = null
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                val header = contactorListAdapter.first(
                    binding.contactorList.firstVisiblePosition,
                    binding.contactorList.lastVisiblePosition
                )
                if (header != null) {
                    binding.contactSidebar.setSelected(header, feedback = false)
                }
                if (header != first) {
                    view?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
                first = header
            }

        })

    }

    override fun onStart() {
        super.onStart()

        val data: List<PeopleGroup> =
            Person.all(this)
                .groupBy { it.first }
                .toSortedMap { o1, o2 -> o1 - o2 }
                .map { PeopleGroup(it.key, it.value) }

        Log.d(javaClass.name, "data size: ${data.sumOf { it.people.size }}")

        contactorListAdapter.update(data)
        Log.d(javaClass.name, "$data")
    }
}