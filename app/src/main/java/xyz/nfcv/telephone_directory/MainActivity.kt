package xyz.nfcv.telephone_directory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter
import xyz.nfcv.telephone_directory.databinding.ActivityMainBinding
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