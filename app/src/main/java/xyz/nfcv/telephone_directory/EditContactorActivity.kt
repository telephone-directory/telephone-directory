package xyz.nfcv.telephone_directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.BaseColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.ofBitmap
import xyz.nfcv.telephone_directory.databinding.ActivityEditContactorBinding
import xyz.nfcv.telephone_directory.model.Person
import java.util.*
import java.util.regex.Pattern

class EditContactorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditContactorBinding
    private lateinit var person: Person

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityEditContactorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime())

            if (windowInsets.isVisible(WindowInsetsCompat.Type.ime())) {
                view.updatePadding(top = bars.top, bottom = ime.bottom)
            } else {
                view.updatePadding(top = bars.top, bottom = bars.bottom)
            }

            WindowInsetsCompat.CONSUMED
        }

        val personId = intent.getStringExtra(BaseColumns._ID)
        if (personId == null) {
            Toast.makeText(this, "Extra??????", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val p = Person.select(this, personId)
        if (p == null) {
            Toast.makeText(this, "Person?????????", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            this.person = p
        }

        binding.editCancel.setOnClickListener {
            finish()
        }

        binding.editCancel.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                MotionEvent.ACTION_UP -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE)
            }
            return@setOnTouchListener false
        }

        binding.contactorName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { binding.contactorAvatar.setImageBitmap("$s".last.ofBitmap()) }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.contactorName.setText(person.name)
        binding.contactorAvatar.setImageBitmap(person.last.ofBitmap())

        binding.contactorPhone.setText(person.telephone)

        person.email?.let { binding.contactorEmail.setText(it) }

        person.workAddress?.let { binding.contactorAddressWork.setText(it) }

        person.homeAddress?.let { binding.contactorAddressHome.setText(it) }

        binding.editSubmit.setOnClickListener {
            val name = binding.contactorName.text.toString().trim()
            val phone = binding.contactorPhone.text.toString().trim()
            val email = binding.contactorEmail.text.toString().trim()
            val addressHome = binding.contactorAddressHome.text.toString().trim()
            val addressWork = binding.contactorAddressWork.text.toString().trim()
            Log.d(
                javaClass.name,
                "contactor(name=$name, phone=$phone, email=$email, home=$addressHome, work=$addressWork)"
            )
            if (name.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show()
            } else {
                person.name = name
                person.telephone = phone
                person.email = email
                person.homeAddress = addressHome
                person.workAddress = addressWork
                Person.update(this, person)
                finish()
            }

        }

        binding.editSubmit.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                MotionEvent.ACTION_UP -> v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE)
            }
            return@setOnTouchListener false
        }
    }

    companion object {
        val String.last: String
            get() {
                val chinese = Pattern.compile("[\u4e00-\u9fa5]").matcher(this).find()
                return if (chinese) {
                    "${this.trim().uppercase().lastOrNull() ?: "#"}"
                } else {
                    val words = this.split(" ").filter { it.isNotBlank() }
                    val l =
                        words.mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
                    if (l.isBlank()) "#" else l
                }
            }
    }

}