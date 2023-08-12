package vara17.loginfirebaseapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import vara17.loginfirebaseapp.*
import vara17.loginfirebaseapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var binding: ActivityLoginBinding

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val googleClient: GoogleApiClient by lazy { getGoogleApiClient() }

    private val RC_GOOGLE_SIGN_IN = 99

    private fun getGoogleApiClient(): GoogleApiClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.buttonLogIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (isValidEmail(email) && isValidPassword(password)) {
                logInByEmail(email, password)
            }
        }

        binding.textViewForgotPassword.setOnClickListener {
            gotoActivity<ForgotPasswordActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        binding.buttonCreateAccount.setOnClickListener {
            gotoActivity<SignUpActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        binding.editTextEmail.validate {
            binding.editTextEmail.error = if (isValidEmail(it)) null else "The email is not valid"
        }

        binding.editTextPassword.validate {
            binding.editTextPassword.error =
                if (isValidPassword(it)) null else "The password is not valid"
        }

        binding.buttonLogInGoogle.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleClient)
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    private fun logInByEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (auth.currentUser!!.isEmailVerified) {
                    toast("User ${auth.currentUser?.email} is now logged in")
                } else {
                    toast("User must confirm email first")
                }
            } else {
                toast("An unexpected error ocurred, please try again")
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        toast("Connection failed")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_GOOGLE_SIGN_IN){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)!!
            if(result.isSuccess){
                val account = result.signInAccount
                loginByGoogleAccountIntoFirebase(account!!)
            }else{
                toast("Error signing in with google")
            }
        }
    }

    private fun loginByGoogleAccountIntoFirebase(googleAccount: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            toast("Sign in by google")
            if(googleClient.isConnected){
                Auth.GoogleSignInApi.signOut(googleClient)
            }
            gotoActivity<MainActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}