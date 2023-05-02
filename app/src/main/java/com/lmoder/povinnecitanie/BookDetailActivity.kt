package com.lmoder.povinnecitanie

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.google.firebase.database.*



class BookDetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var accessRights = false
    private lateinit var name: String
    private lateinit var bookName: TextView
    private lateinit var bookAuthor: TextView
    private lateinit var bookTitle: TextView
    private lateinit var bookDescription: TextView
    private lateinit var bookImage: ImageView
    private lateinit var deleteBook: Button
    private lateinit var deleteQuizButton: Button
    private lateinit var editBookButton: Button
    private lateinit var classCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        name = intent.getStringExtra("name").toString()

        bookTitle = findViewById(R.id.bookDetailTitle)
        bookName = findViewById(R.id.bookDetailName)
        bookAuthor = findViewById(R.id.bookDetailAuthor)
        bookDescription = findViewById(R.id.bookDetailDescription)
        bookImage = findViewById(R.id.bookDetailImage)
        deleteBook = findViewById(R.id.bookDetailDelete)
        deleteQuizButton = findViewById(R.id.bookdetailDeleteQuiz)
        editBookButton = findViewById(R.id.bookDetailEditBook)
        val quizButton = findViewById<Button>(R.id.bookDetailQuizButton)
        val notes = findViewById<ConstraintLayout>(R.id.notesLayout)
        val notesText = findViewById<TextView>(R.id.notesTextView)

        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE) ?: return

        classCode = sharedPref.getString("ClassCode", "1234").toString()

        accessRights = sharedPref.getBoolean(getString(R.string.acces_rights), false)
        notesText.text = sharedPref.getString(name, "No notes yet")

        if (accessRights){
            quizButton.text = "Add quiz"
            notes.visibility = View.GONE
        }else{
            quizButton.text = "Start quiz"
            deleteBook.visibility = View.GONE
            deleteQuizButton.visibility = View.GONE
            editBookButton.visibility = View.GONE
        }

        quizButton.setOnClickListener {
            quizButtonOnClick()
        }

        deleteQuizButton.setOnClickListener {
            deleteQuiz(name)
        }

        editBookButton.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        notes.setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        database = FirebaseDatabase.getInstance().getReference("db/class/$classCode/books")
        database.orderByChild("name").equalTo(name).addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (snapshot.exists())
                {
                    for (bookSnapshot in snapshot.children)
                    {
                        setupBook(bookSnapshot)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })
    }

    private fun setupBook(bookSnapshot: DataSnapshot) {
        val book = bookSnapshot.getValue(Book::class.java)
        val title = book?.author + ": " + book?.name
        bookTitle.text = title
        bookAuthor.text = book?.author
        bookName.text = book?.name
        bookDescription.text = book?.desc
        deleteBook.setOnClickListener {
            bookSnapshot.ref.removeValue()
            val intent = Intent(this@BookDetailActivity,
                MainActivity::class.java)
            startActivity(intent)
        }
        bookImage.load(book?.imageURL){
            placeholder(R.drawable.book)
            error(R.drawable.book)
        }
    }

    private fun quizButtonOnClick(){

        val intent = if (accessRights){
            Intent(this, AddQuizActivity::class.java)
        }else{
            Intent(this, QuizActivity::class.java)
        }

        intent.putExtra("name", name)
        startActivity(intent)
    }

    private fun deleteQuiz(name: String){
        val quizRef = FirebaseDatabase.getInstance().getReference("\"db/class/$classCode/quiz\"")
        val query = quizRef.orderByChild("name").equalTo(name)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (quizSnapshot in dataSnapshot.children) {
                    quizSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }
}