package inu.withus.restructversion

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.databinding.ActivityLoginBinding
import inu.withus.restructversion.dto.LoginDTO
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Firebase Authentication 관리 클래스
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signinAndSignup()
        }
    }

    fun signinAndSignup() {

        val loginDTO = LoginDTO()

        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Creating a user account
                    loginDTO.Id = email_edittext.text.toString()
                    loginDTO.password = password_edittext.text.toString()
                    Log.d(TAG, "ID = ${loginDTO.Id}")
                    Log.d(TAG, "PW = ${loginDTO.password}")
                    Log.d(TAG, "LoginActivity uid = ${auth?.uid}")
                    FirebaseFirestore.getInstance().collection("userInfo").document(auth?.uid!!).set(loginDTO)
                        ?.addOnCompleteListener {
                            Log.d(TAG, "저장 성공")
                            moveMainPage(task.result.user)
                        }
                } else if(task.exception?.message.isNullOrEmpty()) {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    // Login if you have account
                    signinEmail()
                }
            }
    }

    // 로그인 메소드
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener { task->
                if (task.isSuccessful) {
                    // Login
                    Log.d(TAG, "로그인 성공")
                    moveMainPage(auth?.currentUser)
                } else {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?) {
        // User is signed in
        if(user != null) {
            Toast.makeText(this, getString(R.string.signin_complete), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        //자동 로그인 설정
//        moveMainPage(auth?.currentUser)
//    }
}