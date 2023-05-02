package com.lmoder.povinnecitanie

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE)

        val studentLogin = findViewById<ImageButton>(R.id.studentLoginButton)
        val teacherLogin = findViewById<ImageButton>(R.id.teacherLoginButton)
        val loginButton = findViewById<Button>(R.id.appLoginButton)
        val loginCode = findViewById<EditText>(R.id.loginClassCode)
        var isTeacher = false

        teacherLogin.setOnClickListener{
            editBackground(teacherLogin, studentLogin)
            isTeacher = true }

        studentLogin.setOnClickListener {
            editBackground(studentLogin, teacherLogin)
            isTeacher = false }

        loginButton.setOnClickListener {
            login(isTeacher, loginCode.text.toString())
        }

    }

    private fun editBackground(buttonEnable: ImageButton, buttonDisable: ImageButton){
        buttonEnable.setBackgroundResource(R.drawable.login_selected_bg)
        buttonDisable.setBackgroundResource(R.drawable.notes_bg)
    }

    private fun login(isTeacher: Boolean, code: String){

        val database = FirebaseDatabase.getInstance().getReference("db/class")
        val query = database.orderByKey().equalTo(code)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    with (sharedPref.edit()) {
                        putBoolean(getString(com.lmoder.povinnecitanie.R.string.acces_rights), isTeacher)
                        putString("ClassCode", code)
                        apply()
                    }
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }
}