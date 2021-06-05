package xyz.nfcv.telephone_directory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
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
import java.net.URLEncoder

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private lateinit var person: Person
    private lateinit var personId: String

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

        personId = intent.getStringExtra(BaseColumns._ID) ?: ""
        if (personId.isBlank()) {
            Toast.makeText(this, "Extra为空", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    override fun onResume() {
        super.onResume()

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

        binding.detailEdit.setOnClickListener {
            Intent(this, EditContactorActivity::class.java).apply {
                putExtra(BaseColumns._ID, person.id)
            }.apply { startActivity(this) }
        }

        setLike()

        binding.detailPeopleName.text = person.name
        binding.detailPhone.text = person.telephone
        binding.detailPeopleAvatar.setImageBitmap(person.last.ofBitmap())

        binding.detailPhoneCall.setOnClickListener {
            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", person.telephone, null))
                .apply { startActivity(this) }
        }
        binding.detailPhoneMessage.setOnClickListener {
            Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", person.telephone, null))
                .apply { startActivity(this) }
        }

        binding.detailDelete.setOnClickListener {
            Person.delete(this, person)
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            finish()
        }

        val email = person.email
        if (email?.isNotBlank() == true) {
            binding.detailEmailItem.visibility = View.VISIBLE
            binding.detailEmail.text = email
            binding.detailEmailSend.setOnClickListener {
                Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                    .apply { startActivity(this) }
            }
        } else {
            binding.detailEmailItem.visibility = View.GONE
        }

        val workAddress = person.workAddress
        if (workAddress?.isNotBlank() == true) {
            binding.detailWorkAddressItem.visibility = View.VISIBLE
            binding.detailWorkAddress.text = workAddress
            binding.detailWorkAddressNavigate.setOnClickListener {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=${URLEncoder.encode(workAddress, "UTF-8")}")
                )
                    .apply { startActivity(this) }
            }
        } else {
            binding.detailWorkAddressItem.visibility = View.GONE
        }

        val homeAddress = person.homeAddress
        if (homeAddress?.isNotBlank() == true) {
            binding.detailHomeAddressItem.visibility = View.VISIBLE
            binding.detailHomeAddress.text = homeAddress
            binding.detailHomeAddressNavigate.setOnClickListener {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=${URLEncoder.encode(homeAddress, "UTF-8")}")
                )
                    .apply { startActivity(this) }
            }
        } else {
            binding.detailHomeAddressItem.visibility = View.GONE
        }
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