package com.lmoder.povinnecitanie

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var booksArray: ArrayList<Book>
    private lateinit var addBook: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bookRecyclerView = findViewById(R.id.booksRecV)
        addBook = findViewById(R.id.addBookButton)

        retrieveData(bookRecyclerView)

        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE) ?: return

        val accessRights = sharedPref.getBoolean(getString(R.string.acces_rights), false)

        if (accessRights){
            addBook.visibility = View.VISIBLE
        }else{
            addBook.visibility = View.INVISIBLE
        }

        addBook.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

    }


    private fun retrieveData(recyclerView: RecyclerView){
        booksArray = arrayListOf()

        val classCode = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE).getString("ClassCode", "1234")
        database = FirebaseDatabase.getInstance().getReference("db/class/$classCode/books")
        database.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                booksArray.clear()
                if (snapshot.exists())
                {
                    for (bookSnapshot in snapshot.children)
                    {
                        val book = bookSnapshot.getValue(Book::class.java)
                        booksArray.add(book!!)
                    }
                }
                recyclerView.adapter = RecyclerViewAdapter(booksArray, this@MainActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })

        bookRecyclerView.layoutManager = GridLayoutManager(this, 2)
    }
}