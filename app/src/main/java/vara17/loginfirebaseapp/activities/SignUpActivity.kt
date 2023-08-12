package vara17.loginfirebaseapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import vara17.loginfirebaseapp.*
import vara17.loginfirebaseapp.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViews()
    }

    private fun setUpViews() {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()
    }

    private fun setListeners() {
        binding.buttonGoLogin.setOnClickListener {
            gotoActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()
            if (isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password, confirmPassword)) {
                signUpByEmail(email, password)
            } else {
                toast("Please fill all the data is correct.")
            }
        }

        binding.editTextEmail.validate {
            binding.editTextEmail.error = if (isValidEmail(it)) null else "The email is not valid"
        }

        binding.editTextPassword.validate {
            binding.editTextPassword.error =
                if (isValidPassword(it)) null else "The password is not valid"
        }

        binding.editTextConfirmPassword.validate {
            binding.editTextConfirmPassword.error = if (isValidConfirmPassword(binding.editTextPassword.text.toString(), it)) null else "The confirm password is not valid"
        }
    }

    private fun signUpByEmail(email: String, password: String) {
        auth.let {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        auth.currentUser!!.sendEmailVerification().addOnCompleteListener(this){
                            toast("An email has been sent to you. Please, confirm before sign in.")
                            gotoActivity<LoginActivity> {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                    } else {
                        toast("An unexpected error ocurred, please try again.")
                    }
                }
        }
    }
}