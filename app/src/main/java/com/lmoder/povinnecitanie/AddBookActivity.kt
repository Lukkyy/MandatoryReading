package com.lmoder.povinnecitanie

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddBookActivity : AppCompatActivity() {

    private lateinit var save: Button
    private lateinit var name: EditText
    private lateinit var author: EditText
    private lateinit var desc: EditText
    private lateinit var img: EditText
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        database = Firebase.database.reference

        val bookName = intent.getStringExtra("name")

        save = findViewById(R.id.addBookButton)
        name = findViewById(R.id.nameEditText)
        author = findViewById(R.id.authorEditText)
        desc = findViewById(R.id.descriptionEditText)
        img = findViewById(R.id.imgEditText)

        if (bookName != null){
            addExistingInfo(bookName)
        }

        save.setOnClickListener {
            if (getInfo()){
                saveBook(Book(name.text.toString(), author.text.toString(),
                         desc.text.toString(), img.text.toString()), null)
            }
        }
    }

    private fun addExistingInfo(bookName: String) {
        val classCode = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE).getString("ClassCode", "1234")
        val quizRef = FirebaseDatabase.getInstance()
                     .getReference("db/class/$classCode/books")
        val query = quizRef.orderByChild("name").equalTo(bookName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (quizSnapshot in dataSnapshot.children) {
                    val book = quizSnapshot.getValue(Book::class.java)
                    name.setText(book!!.name)
                    author.setText(book.author)
                    desc.setText(book.desc)
                    img.setText(book.imageURL)
                    save.setOnClickListener {
                        if (getInfo()){
                            saveBook(Book(name.text.toString(), author.text.toString(),
                                desc.text.toString(), img.text.toString()), quizSnapshot.ref)
                        }
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }

    private fun saveBook(book: Book, DBref: DatabaseReference?) {
        val classCode = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE).getString("ClassCode", "1234")
        val ref = DBref ?: database.child("db/class/$classCode/books").push()
        ref.setValue(book)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getInfo(): Boolean {
        if (name.text.isNullOrBlank() || author.text.isNullOrBlank() ||
            desc.text.isNullOrBlank() || img.text.isNullOrBlank()){
            Toast.makeText(this, "Fill in all info", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}