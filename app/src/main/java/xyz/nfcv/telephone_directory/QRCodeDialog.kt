package xyz.nfcv.telephone_directory

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import xyz.nfcv.telephone_directory.databinding.DialogQrcodeBinding
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.util.urlencode
import java.io.File


class QRCodeDialog(val person: Person) : DialogFragment() {

    private lateinit var binding: DialogQrcodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogQrcodeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scheme = getString(R.string.scheme)
        val host = getString(R.string.host)
        val path = getString(R.string.path)
        val uri = Uri.parse("$scheme://$host$path?data=${Gson().toJson(person).urlencode}")
        val size = 512
        val matrix = QRCodeWriter().encode(uri.toString(), BarcodeFormat.QR_CODE, size, size)
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            val offset: Int = y * size
            for (x in 0 until size) {
                pixels[offset + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            .apply { setPixels(pixels, 0, size, 0, 0, size, size) }
        binding.qrcodeImage.setImageBitmap(bitmap)

        binding.qrcodeShare.setOnClickListener {
            val name = "QR_${person.name}_${System.currentTimeMillis() / 1000}"
            val cache = File(requireActivity().cacheDir, name)
            cache.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
            }

            Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(requireContext(), "xyz.nfcv.telephone_directory.fileprovider", cache))
            }.let { startActivity(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        val dm = this.resources.displayMetrics
        requireDialog().window?.let {
            it.setLayout(dm.widthPixels, it.attributes.height)
            it.attributes = it.attributes.apply { gravity = Gravity.CENTER }
        }
    }
}