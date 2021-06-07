package xyz.nfcv.telephone_directory

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.ofBitmap
import xyz.nfcv.telephone_directory.data.TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson
import xyz.nfcv.telephone_directory.databinding.ActivityNfcReadBinding
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.util.hex


class NfcReadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNfcReadBinding
    private var nfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityNfcReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun onStart() {
        super.onStart()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        Log.d(javaClass.name, "TAG ID: ${tag?.id?.hex?.uppercase()}")
        tag?.let {
            val uri = intent.data
            val data = uri?.getQueryParameter("data")
            val person: Person? = Gson().fromJson(data, object : TypeToken<Person>() {}.type)
            if (person != null) {
                binding.nfcCard.detailPeopleName.text = person.name
                binding.nfcCard.detailPhone.text = person.telephone
                binding.nfcCard.detailPeopleAvatar.setImageBitmap(person.last.ofBitmap())
                binding.nfcCard.detailEmail.text = person.email ?: "无"
                binding.nfcCard.detailWorkAddress.text = person.workAddress ?: "无"
                binding.nfcCard.detailHomeAddress.text = person.homeAddress ?: "无"
                binding.nfcCard.detailAddToContactorList.setOnClickListener {
                    Intent(this, AddContactorActivity::class.java).apply {
                        putExtra(TPerson.COLUMN_NAME_NAME, person.name)
                        putExtra(TPerson.COLUMN_NAME_TELEPHONE, person.telephone)
                        putExtra(TPerson.COLUMN_NAME_EMAIL, person.email)
                        putExtra(TPerson.COLUMN_NAME_WORK_ADDRESS, person.workAddress)
                        putExtra(TPerson.COLUMN_NAME_HOME_ADDRESS, person.homeAddress)
                    }.apply {
                        startActivity(this)
                        finish()
                    }
                }
                binding.nfcNote.visibility = View.GONE
                binding.nfcCardParent.visibility = View.VISIBLE
            } else {
                binding.nfcNote.visibility = View.VISIBLE
                binding.nfcCardParent.visibility = View.GONE
            }
        }
    }
}