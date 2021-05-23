package xyz.nfcv.telephone_directory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import xyz.nfcv.telephone_directory.adapter.SearchContactorAdapter
import xyz.nfcv.telephone_directory.databinding.ActivitySearchBinding
import xyz.nfcv.telephone_directory.model.Person

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchContactorAdapter: SearchContactorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        searchContactorAdapter = SearchContactorAdapter(this, binding.contactorSearchList) {position: Int, person: Person ->
            Log.d(javaClass.name, "item: $position $person")
        }

        binding.contactorSearchCancel.setOnClickListener {
            finish()
        }

        binding.contactorSearch.requestFocus()

        binding.contactorSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { searchContactorAdapter.update(it.toString()) }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}