package xyz.nfcv.telephone_directory

import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.ofBitmap
import xyz.nfcv.telephone_directory.databinding.ActivityDetailBinding
import xyz.nfcv.telephone_directory.model.Person

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding

    lateinit var person: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val personId = intent.getStringExtra(BaseColumns._ID)
        if (personId == null) {
            Toast.makeText(this, "Extra为空", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val p = Person.select(this, personId)
        if (p == null) {
            Toast.makeText(this, "Person不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            this.person = p
        }

        Log.d(javaClass.name, "person=$p")

        binding.detailLike.setOnClickListener {
            if (person.like > 0) {
                person.like = 0
            } else {
                person.like = 1
            }
            Person.update(this, person)
            setLike()
        }

        binding.detailPeopleName.text = person.name
        binding.detailPeopleAvatar.setImageBitmap(person.last.ofBitmap())
    }

    override fun onStart() {
        super.onStart()
        setLike()
    }

    private fun setLike() {
        if (person.like > 0) {
            binding.detailLike.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ResourcesCompat.getDrawable(resources, R.drawable.ic_like, null),
                null,
                null
            )
        } else {
            binding.detailLike.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ResourcesCompat.getDrawable(resources, R.drawable.ic_dislike, null),
                null,
                null
            )
        }
    }
}