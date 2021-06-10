package xyz.nfcv.telephone_directory

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import xyz.nfcv.telephone_directory.LoginDialog.State.LOGIN
import xyz.nfcv.telephone_directory.LoginDialog.State.REGISTER
import xyz.nfcv.telephone_directory.data.CloudApi
import xyz.nfcv.telephone_directory.databinding.DialogLoginBinding
import xyz.nfcv.telephone_directory.model.User

class LoginDialog(
    private val cloud: CloudApi,
    private val callback: (User) -> Unit
) : DialogFragment() {
    enum class State {
        LOGIN,
        REGISTER
    }

    private lateinit var binding: DialogLoginBinding
    private var state: State = LOGIN
        set(value) {
            field = value
            when (field) {
                LOGIN -> {
                    binding.loginButtonLogin.text = getText(R.string.login)
                    binding.loginTextCreate.text = getText(R.string.create_account)
                }
                REGISTER -> {
                    binding.loginButtonLogin.text = getText(R.string.register)
                    binding.loginTextCreate.text = getText(R.string.login_account)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginTextCreate.setOnClickListener {
            state = when (state) {
                LOGIN -> REGISTER
                REGISTER -> LOGIN
            }
        }

        binding.loginButtonLogin.setOnClickListener {
            val username = binding.loginEditUsername.string
            val password = binding.loginEditPassword.string
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            } else {
                when (state) {
                    LOGIN -> {
                        cloud.login(username, password)
                            .enqueue(CloudApi.defaultCallback { code, user ->
                                if (code != 200 || user == null) {
                                    Toast.makeText(requireContext(), "登录失败，请检查用户名或密码", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show()
                                    callback(user)
                                    dismiss()
                                }
                            })
                    }
                    REGISTER -> {
                        cloud.register(username, password)
                            .enqueue(CloudApi.defaultCallback { code: Int, token: String? ->
                                if (code != 200 || token.isNullOrBlank()) {
                                    Toast.makeText(requireContext(), "注册失败，用户名重复", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show()
                                    state = LOGIN
                                }
                            })
                    }
                }
            }
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

    companion object {
        private val EditText?.string: String
            get() = this?.text.toString()
    }
}