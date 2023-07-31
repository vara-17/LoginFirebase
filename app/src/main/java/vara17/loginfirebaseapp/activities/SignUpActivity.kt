package vara17.loginfirebaseapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import vara17.loginfirebaseapp.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViews()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User is NOT logged in", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "User is logged in", Toast.LENGTH_LONG).show()
        }


        createAccount("varaserrano.andres@gmail.com", "123456")
    }

    private fun setUpViews() {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



    }

    private fun createAccount(email: String, password: String) {
        auth?.let {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            this,
                            "createUserWithEmailAndPassword: succes",
                            Toast.LENGTH_LONG
                        ).show()
                        val user = auth.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this,
                            "createUserWithEmailAndPassword: fail",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}