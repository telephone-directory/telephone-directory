package xyz.nfcv.telephone_directory

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.nfcv.telephone_directory.databinding.ActivityNfcTagBinding
import java.nio.charset.Charset


class NfcTagActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNfcTagBinding
    private lateinit var pendingIntent: PendingIntent
    private var nfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val intent = Intent(this, javaClass).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

}