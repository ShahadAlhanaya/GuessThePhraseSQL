package com.example.guessthephrasesql

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var mainActivityLayout: ConstraintLayout
    lateinit var guessButton: Button
    lateinit var playAgainButton: Button
    lateinit var guessEditText: EditText
    lateinit var promptTextView: TextView
    lateinit var phraseTextView: TextView
    lateinit var guessedLettersTextView: TextView
    lateinit var bestScoreTextView: TextView

    var phrase = "Hello World"
    var phraseList = arrayListOf<String>()
    private val phraseDictionary = mutableMapOf<Int, Char>()
    private var userGuess = ""
    private var userLetterGuess = ""
    private var count = 0
    var phraseStage = true
    var bestScore = 0

    private lateinit var sharedPreferences: SharedPreferences


    private lateinit var messageArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //actionbar
        val actionbar = supportActionBar!!
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)


        mainActivityLayout = findViewById(R.id.mainActivityLayout_cl)
        guessButton = findViewById(R.id.guess_button)
        playAgainButton = findViewById(R.id.playAgain_button)
        guessEditText = findViewById(R.id.guess_editText)
        promptTextView = findViewById(R.id.prompt_textview)
        phraseTextView = findViewById(R.id.phrase_textview)
        guessedLettersTextView = findViewById(R.id.guessedLetters_textview)
        bestScoreTextView = findViewById(R.id.bestScore)

        sharedPreferences = this.getSharedPreferences("Best Score", Context.MODE_PRIVATE)
        bestScore = sharedPreferences.getInt("Best Score", 0)

        phrase = getRandomPhrase()

        messageArrayList = ArrayList()

        messages_recyclerView.adapter = MessagesRecyclerViewAdapter(messageArrayList)
        messages_recyclerView.layoutManager = LinearLayoutManager(this)

        playAgainButton.isVisible = false
        guessedLettersTextView.isVisible = false
        if( bestScore == 0){
            bestScoreTextView.isVisible = false
        }else{
            bestScoreTextView.text = "Best Score: $bestScore"
        }

        guessButton.setOnClickListener { addMessage() }
        playAgainButton.setOnClickListener { this.recreate() }

        for (i in phrase.indices) {
            if (phrase[i] == ' ') {
                phraseDictionary[i] = ' '
                userGuess += ' '
            } else {
                phraseDictionary[i] = '*'
                userGuess += '*'
            }
        }
        updateText()

    }

    private fun getRandomPhrase(): String{
        phraseList = intent.extras!!.getStringArrayList("phraseList") as ArrayList<String>
        if(phraseList.isNotEmpty()) {
            val random = Random.nextInt(0, phraseList.size)
            return phraseList[random]
        }
        return "Hello World"
    }

    private fun addMessage() {
        var guess = guessEditText.text.toString().lowercase()

        if (phraseStage) {
            if (guess == phrase.lowercase()) {
                showDialog("You won!", "you guessed correctly!")
                updateScore()
                guessEditText.isVisible = false
                guessButton.isVisible = false
                playAgainButton.isVisible = true
            } else {
                messageArrayList.add("Wrong phrase: $guess")
                phraseStage = false
                updateText()
            }
        } else {
            if (guess.isNotEmpty()) {
                userGuess = ""
                checkLetters(guess)
            } else {
                Snackbar.make(
                    mainActivityLayout,
                    "Please enter one letter at least",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        guessEditText.text.clear()
        messages_recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun checkLetters(guess: String) {
        val guessedLetter = guess.first()
        var found = 0
        for (i in phrase.indices) {
            if (phrase[i].lowercase() == guessedLetter.lowercase()) {
                phraseDictionary[i] = guessedLetter.lowercaseChar()
                found++
            }
        }
        for (i in phraseDictionary) {
            userGuess += phraseDictionary[i.key]
        }

        if (userGuess.lowercase() == phrase.lowercase()) {
            showDialog("You won!", "you guessed correctly!")
            updateScore()
            guessEditText.isVisible = false
            guessButton.isVisible = false
            playAgainButton.isVisible = true
            return
        }
        if (userLetterGuess.isEmpty()) {
            userLetterGuess += guessedLetter
        } else {
            "$userLetterGuess,  $guessedLetter"
        }
        if (found > 0) {
            messageArrayList.add("Found $found ${guessedLetter.toUpperCase()}(s)")
        } else {
            messageArrayList.add("No ${guessedLetter.toUpperCase()}s found")
        }
        count++
        val guessesLeft = 10 - count
        if (count < 10) {
            messageArrayList.add("$guessesLeft guesses remaining")
        }

        updateText()
        messages_recyclerView.scrollToPosition(messageArrayList.size - 1)

        if (count == 10) {
            showDialog("You Lost", "")
            guessEditText.isVisible = false
            guessButton.isVisible = false
            playAgainButton.isVisible = true
        }
    }

    private fun updateScore(){
        bestScore = (10 - count) * 10
        bestScoreTextView.isVisible= true
        bestScoreTextView.text = "Best Score: $bestScore"
        with(sharedPreferences.edit()) {
            putInt("Best Score", bestScore)
            apply()
        }
    }

    private fun updateText() {
        phraseTextView.text = "Phrase:  $userGuess"
        guessedLettersTextView.text = "Guessed Letters:  $userLetterGuess"
        if (phraseStage) {
            guessEditText.hint = "Guess the full phrase"
            promptTextView.text = "Guess the full phrase"

        } else {
            guessedLettersTextView.isVisible = true
            promptTextView.text = "Guess a letter"
            guessEditText.hint = "Guess a letter"
        }
    }


    private fun showDialog(title: String, message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("$message\nThe phrase was $phrase\nDo you want to play again?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                this.recreate()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}