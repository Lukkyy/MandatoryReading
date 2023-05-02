package com.lmoder.povinnecitanie

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EdgeEffect
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddQuizActivity : AppCompatActivity() {

    private lateinit var saveQuiz: Button
    private lateinit var nextQuestion: Button
    private lateinit var question: EditText
    private lateinit var correctAnswer: EditText
    private lateinit var secondAnswer: EditText
    private lateinit var thirdAnswer: EditText
    private lateinit var database: DatabaseReference

    private var questions = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz)

        val name = intent.extras!!.getString("name")
        if(name == null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        database = Firebase.database.reference

        saveQuiz = findViewById(R.id.saveBookButton)
        nextQuestion = findViewById(R.id.addQuestionButton)
        question = findViewById(R.id.questionEditText)
        correctAnswer = findViewById(R.id.correctAnswerEditText)
        secondAnswer = findViewById(R.id.secondAnswerEditText)
        thirdAnswer = findViewById(R.id.thirdAnswerEditText)

        saveQuiz.setOnClickListener {
            questions.add(getInfo())
            val quiz = Quiz(name, questions)
            pushQuiz(quiz, name!!)

        }

        nextQuestion.setOnClickListener {
            questions.add(getInfo())
            clearInputs()
        }
    }

    private fun clearInputs() {
        question.text.clear()
        correctAnswer.text.clear()
        secondAnswer.text.clear()
        thirdAnswer.text.clear()
    }

    private fun pushQuiz(quiz: Quiz, name: String) {
        val classCode = this.getSharedPreferences(getString(R.string.shared_pref),
            Context.MODE_PRIVATE).getString("ClassCode", "1234")
        val ref = database.child("db/class/$classCode/quiz").push()
        ref.setValue(quiz)

        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    private fun getInfo(): Question {
        return Question(question.text.toString(), correctAnswer.text.toString(),
                        secondAnswer.text.toString(), thirdAnswer.text.toString())
    }

    private fun isFilled(): Boolean {
        if (question.text.isNullOrBlank() || correctAnswer.text.isNullOrBlank() ||
            secondAnswer.text.isNullOrBlank() || thirdAnswer.text.isNullOrBlank()){
            Toast.makeText(this, "Fill in all info", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}