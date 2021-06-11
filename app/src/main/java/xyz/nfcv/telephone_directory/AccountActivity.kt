package xyz.nfcv.telephone_directory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.JobIntentService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import xyz.nfcv.telephone_directory.data.Account
import xyz.nfcv.telephone_directory.data.CloudApi
import xyz.nfcv.telephone_directory.databinding.ActivityAccountBinding
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.telephone_directory.model.User

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private val cloud: CloudApi by lazy { CloudApi.retrofit.create(CloudApi::class.java) }

    private val user: User?
        get() {
            return Account.get(this)
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }


        user.let {
            if (it == null) {
                binding.accountId.text = getText(R.string.no_login)
                binding.accountLogout.text = getText(R.string.login)
            } else {
                binding.accountId.text = "UserId: " + it.userId
                binding.accountLogout.text = getText(R.string.logout)
            }
        }

        binding.accountLogout.setOnClickListener {
            val u = user
            if (u == null) {
                LoginDialog(cloud) { user ->
                    Account.take(this, user)
                    binding.accountLogout.text = getText(R.string.logout)
                    binding.accountId.text = "UserId: " + user.userId
                }.show(supportFragmentManager, LoginDialog::javaClass.name)
            } else {
                Account.remove(this)
                Person.clear(this)
                binding.accountLogout.text = getText(R.string.login)
                binding.accountId.text = getText(R.string.no_login)
                LoginDialog(cloud) { user ->
                    Account.take(this, user)
                    binding.accountLogout.text = getText(R.string.logout)
                    binding.accountId.text = "UserId: " + user.userId
                }.show(supportFragmentManager, LoginDialog::javaClass.name)
            }
        }

        binding.accountSync.setOnClickListener {
            val u = user
            if (u == null) {
                Toast.makeText(this, getText(R.string.no_login), Toast.LENGTH_SHORT).show()
            } else {
                JobIntentService.enqueueWork(this, CloudService::class.java, 1, Intent())
            }
        }
    }
}