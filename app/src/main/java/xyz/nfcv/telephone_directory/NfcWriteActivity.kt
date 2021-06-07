package xyz.nfcv.telephone_directory

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.gson.Gson
import xyz.nfcv.telephone_directory.databinding.ActivityNfcWriteBinding
import xyz.nfcv.telephone_directory.model.Person

class NfcWriteActivity : AppCompatActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var person: Person
    private lateinit var personId: String
    private lateinit var binding: ActivityNfcWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityNfcWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        personId = intent.getStringExtra(BaseColumns._ID) ?: ""
        if (personId.isBlank()) {
            Toast.makeText(this, "Extra为空", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            val scheme = getString(R.string.scheme)
            val host = getString(R.string.host)
            val path = getString(R.string.path)
            val uri = Uri.parse("$scheme://$host$path?data=${Gson().toJson(person)}")
            val message = NdefMessage(arrayOf(NdefRecord.createUri(uri)))
            if (tag.write(message)) {
                Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun Tag.write(message: NdefMessage): Boolean {

        val ndef = Ndef.get(this)

        if (ndef == null) {
            val formatable = NdefFormatable.get(this)
            if (formatable == null) {
                Log.d(javaClass.name, "formatable == null")
            } else {
                try {
                    Log.d(javaClass.name, "formatable connect")
                    formatable.connect()
                    if (formatable.isConnected) {
                        formatable.format(message)
                        return true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    Log.d(javaClass.name, "formatable close")
                    formatable.close()
                }
            }
        } else {
            try {
                Log.d(javaClass.name, "ndef connect")
                ndef.connect()
                if (ndef.isConnected && ndef.isWritable) {
                    ndef.writeNdefMessage(message)
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                Log.d(javaClass.name, "ndef close")
                ndef.close()
            }
        }

        return false
    }

    override fun onStart() {
        super.onStart()
        val p = Person.select(this, personId)
        if (p == null) {
            Toast.makeText(this, "Person不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            this.person = p
        }

        Log.d(javaClass.name, "person=$p")
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent =
            PendingIntent.getActivity(this, 0, Intent(this, NfcWriteActivity::class.java), 0)
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this)
    }
}