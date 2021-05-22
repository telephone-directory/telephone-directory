package xyz.nfcv.telephone_directory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import xyz.nfcv.telephone_directory.databinding.ActivityEditContactorBinding

class EditContactorActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditContactorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityEditContactorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }


        
    }
}