package com.example.buynow

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.buynow.Utils.Extensions.toast
import com.example.buynow.Utils.FirebaseUtils.firebaseAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInBtn: Button
    lateinit var emailEt: EditText
    lateinit var passEt: EditText

    lateinit var loadingDialog: loadingDialog

    lateinit var emailError:TextView
    lateinit var passwordError:TextView

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val forgottenPasswordTv = findViewById<TextView>(R.id.forgottenPassTv)

        val signUpTv = findViewById<TextView>(R.id.signUpTv)
        signInBtn = findViewById(R.id.loginBtn)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.PassEt)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)
        auth = Firebase.auth



        textAutoCheck()

        loadingDialog = loadingDialog(this)

        signUpTv.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        forgottenPasswordTv.setOnClickListener {
            Toast.makeText(this@LoginActivity,"Checking for forgotten password...",Toast.LENGTH_SHORT).show()

            if (emailEt.text.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(emailEt.text.toString())
                    .addOnCompleteListener { task ->
                        Toast.makeText(this@LoginActivity,"Starting task",Toast.LENGTH_LONG).show()

                        if (task.isSuccessful) {
                            forgottenPasswordTv.text = "Reset Email Sent!"
                            Toast.makeText(this@LoginActivity,"Email sent",Toast.LENGTH_LONG).show()

                        } else {
                            forgottenPasswordTv.text = "Email failure"
                            Toast.makeText(this@LoginActivity,"Failure",Toast.LENGTH_LONG).show()

                        }
                    }.addOnFailureListener { task ->
                        Toast.makeText(this@LoginActivity,"Failure",Toast.LENGTH_LONG).show()
                    }

            } else {
                Toast.makeText(this@LoginActivity,"Please fill in the email field first",Toast.LENGTH_LONG).show()
            }




        }

        signInBtn.setOnClickListener {
            checkInput()


        }


    }

    private fun textAutoCheck() {



        emailEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()){
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (passEt.text.length > 4){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,R.drawable.ic_check), null)

                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                passwordError.visibility = View.GONE
                if (count > 4){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,R.drawable.ic_check), null)

                }
            }
        })



    }

    private fun checkInput() {

        if (emailEt.text.isEmpty()){
            emailError.visibility = View.VISIBLE
            emailError.text = "Email Can't be Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Enter Valid Email"
            return
        }
        if(passEt.text.isEmpty()){
            passwordError.visibility = View.VISIBLE
            passwordError.text = "Password Can't be Empty"
            return
        }

        if ( passEt.text.isNotEmpty() && emailEt.text.isNotEmpty()){
            emailError.visibility = View.GONE
            passwordError.visibility = View.GONE
            signInUser()
        }
    }


    private fun signInUser() {

        loadingDialog.startLoadingDialog()
        signInEmail = emailEt.text.toString().trim()
        signInPassword = passEt.text.toString().trim()

            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {

                        loadingDialog.dismissDialog()
                        startActivity(Intent(this, HomeActivity::class.java))
                        toast("signed in successfully")
                        finish()

                        /*
                        if(FirebaseUtils.firebaseUser?.isEmailVerified == true){
                            startActivity(Intent(this, HomeActivity::class.java))
                            loadingDialog.dismissDialog()
                            toast("signed in successfully")
                            finish()
                        }
                        else {
                            loadingDialog.dismissDialog()
                            val intent = Intent(this, EmailVerifyActivity::class.java)
                            intent.putExtra("EmailAddress", emailEt.text.toString().trim())
                            intent.putExtra("loginPassword", passEt.text.toString().trim())
                            startActivity(intent)
                        }

                        */

                    } else {
                        toast("sign in failed")
                        loadingDialog.dismissDialog()
                    }
                }
        }



}