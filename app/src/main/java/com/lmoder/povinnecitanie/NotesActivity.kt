package com.lmoder.povinnecitanie

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val notesText = findViewById<EditText>(R.id.notesEditText)
        val saveNotes = findViewById<Button>(R.id.saveNotesButton)

        val name = intent.extras!!.getString("name")

        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE) ?: return

        saveNotes.setOnClickListener {
            with (sharedPref.edit()) {
                putString(name, notesText.text.toString())
                apply()
            }
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }
}