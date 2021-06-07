package xyz.nfcv.telephone_directory

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.ofBitmap
import xyz.nfcv.telephone_directory.data.TelephoneDirectoryDbHelper.TelephoneDirectory.TPerson
import xyz.nfcv.telephone_directory.databinding.ActivityNfcTagBinding
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.util.hex


class NfcTagActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNfcTagBinding
    private var nfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        Log.d(javaClass.name, "TAG ID: ${tag?.id?.hex?.uppercase()}")
        tag?.let {
            binding.nfcNote.visibility = View.GONE
            binding.nfcCardParent.visibility = View.VISIBLE
            val uri = intent.data
            val name = uri?.getQueryParameter(TPerson.COLUMN_NAME_NAME) ?: ""
            val telephone = uri?.getQueryParameter(TPerson.COLUMN_NAME_TELEPHONE) ?: ""
            val email = uri?.getQueryParameter(TPerson.COLUMN_NAME_EMAIL)
            val addressWork = uri?.getQueryParameter(TPerson.COLUMN_NAME_WORK_ADDRESS)
            val addressHome = uri?.getQueryParameter(TPerson.COLUMN_NAME_HOME_ADDRESS)
            val person = Person(null, name, telephone, email, addressWork, addressHome)
            binding.nfcCard.detailPeopleName.text = person.name
            binding.nfcCard.detailPeopleAvatar.setImageBitmap(person.last.ofBitmap())
            binding.nfcCard.detailPhone.text = person.telephone ?: "无"
            binding.nfcCard.detailEmail.text = person.email ?: "无"
            binding.nfcCard.detailWorkAddress.text = person.workAddress ?: "无"
            binding.nfcCard.detailHomeAddress.text = person.homeAddress ?: "无"
        }
    }
}