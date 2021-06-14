package xyz.nfcv.telephone_directory

import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.ofBitmap
import xyz.nfcv.telephone_directory.data.TelephoneDirectoryDbHelper
import xyz.nfcv.telephone_directory.databinding.ActivityUriBrowserBinding
import xyz.nfcv.telephone_directory.model.Person


class UriBrowserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUriBrowserBinding
    private var nfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityUriBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        Log.d(javaClass.name, "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(javaClass.name, "onStart")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val person = intent?.data?.read()
        if (person != null) {
            binding.personCard.detailPeopleName.text = person.name
            binding.personCard.detailPhone.text = person.telephone
            binding.personCard.detailPeopleAvatar.setImageBitmap(person.last.ofBitmap())
            binding.personCard.detailEmail.text = person.email ?: "无"
            binding.personCard.detailWorkAddress.text = person.workAddress ?: "无"
            binding.personCard.detailHomeAddress.text = person.homeAddress ?: "无"
            binding.personCard.detailAddToContactorList.setOnClickListener {
                Intent(this, AddContactorActivity::class.java).apply {
                    putExtra(TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson.COLUMN_NAME_NAME, person.name)
                    putExtra(TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson.COLUMN_NAME_TELEPHONE, person.telephone)
                    putExtra(TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson.COLUMN_NAME_EMAIL, person.email)
                    putExtra(TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson.COLUMN_NAME_WORK_ADDRESS, person.workAddress)
                    putExtra(TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson.COLUMN_NAME_HOME_ADDRESS, person.homeAddress)
                }.apply {
                    startActivity(this)
                    finish()
                }
            }
        } else {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(javaClass.name, "onStop")
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(javaClass.name, "onDestroy")
    }

    override fun onResume() {
        super.onResume()
        Log.d(javaClass.name, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(javaClass.name, "onPause")
    }

    private fun Uri.read(): Person? {
        val data = this.getQueryParameter("data")
        return Gson().fromJson(data, object : TypeToken<Person>() {}.type)
    }
}