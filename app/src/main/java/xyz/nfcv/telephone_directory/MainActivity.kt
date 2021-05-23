package xyz.nfcv.telephone_directory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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

        for (i in 0..100) {
            Person.insert(
                this,
                Person(null, "${('A'..'Z').random()} ${('A'..'Z').random()}", "1276512")
            )
        }

        for (i in 0..5) {
            Person.insert(
                this,
                Person(null, "${('0'..'9').random()} ${('A'..'Z').random()}", "1276512")
            )
        }

        binding.contactorSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.contactorSearchCancel.visibility = View.VISIBLE
            } else {
                binding.contactorSearchCancel.visibility = View.GONE
            }
        }

        binding.contactorSearchCancel.setOnClickListener {
            if (currentFocus == binding.contactorSearch) {
                currentFocus?.let {
                    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        it.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
                binding.contactorSearch.setText("")
                binding.contactorSearch.clearFocus()
            }

        }

        contactorListAdapter = ContactorListAdapter(this, binding.contactorList)

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

        binding.contactSidebar.addListener { _, header ->
//            if (contactorListAdapter.contains(header)) {
//                Log.d(javaClass.name, "$header")
//                contactorListAdapter.scroll(header)
//            }
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

        contactorListAdapter.update(data)
        Log.d(javaClass.name, "$data")
    }
}