package com.lmoder.povinnecitanie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*

class QuizActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var questions: MutableList<Button>
    private lateinit var name: String
    private lateinit var questionText: TextView
    private lateinit var firstAnswer: Button
    private lateinit var secondAnswer: Button
    private lateinit var thirdAnswer: Button
    private lateinit var currentQuestion: Question
    private var questionNum: Int = 0
    private lateinit var quiz: Quiz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        name = intent.extras?.getString("name").toString()

        questionText = findViewById(R.id.quizQuestionText)

        firstAnswer = findViewById(R.id.quizFirstAnswerButton)
        secondAnswer = findViewById(R.id.quizSecondAnswerButton)
        thirdAnswer = findViewById(R.id.quizThirdAnswerButton)

        questions = mutableListOf(firstAnswer, secondAnswer, thirdAnswer)
        questions.shuffle()

        database = FirebaseDatabase.getInstance().getReference("db/class/1234/quiz")
        database.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        for (bookSnapshot in snapshot.children) {
                            quiz = bookSnapshot.getValue(Quiz::class.java)!!
                            nextQuestion()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@QuizActivity, "NOT WORKING", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun isCorrect(num: Int){
        val response: String = if(num == 0){
            "CORRECT"
        }else{
            "WRONG"
        }
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
    }

    private fun nextQuestion() {
        if (questionNum >= quiz.questions!!.size){
            val intent = Intent(this@QuizActivity, BookDetailActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }else{
            currentQuestion = quiz.questions!![questionNum]
            questionText.text = currentQuestion.question
            setAnswers(currentQuestion)

            questionNum++
        }
    }

    private fun setAnswers(currentQuestion: Question) {
        questions[0].text = currentQuestion.correctAnswer
        questions[0].setOnClickListener {
            isCorrect(0)
            questions.shuffle()
            nextQuestion()
        }
        questions[1].text = currentQuestion.secondAnswer
        questions[1].setOnClickListener {
            isCorrect(1)
            questions.shuffle()
            nextQuestion()
        }
        questions[2].text = currentQuestion.thirdAnswer
        questions[2].setOnClickListener {
            isCorrect(2)
            questions.shuffle()
            nextQuestion()
        }
    }
}
