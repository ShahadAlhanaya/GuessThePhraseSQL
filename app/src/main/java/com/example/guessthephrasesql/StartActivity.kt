package com.example.guessthephrasesql

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlin.random.Random

class StartActivity : AppCompatActivity() {

    lateinit var playButton: Button
    lateinit var addButton: Button
    lateinit var dbHelper: DBHelper

    var phraseList = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //initialize DB
        dbHelper = DBHelper(applicationContext)

        playButton = findViewById(R.id.play_button)
        addButton = findViewById(R.id.addPhrase_button)

        playButton.setOnClickListener {
            phraseList = dbHelper.retrieveAllPhrase()
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("phraseList", phraseList)
            startActivity(intent)
        }

        addButton.setOnClickListener {
            showAddDialog(this)
        }


    }


    private fun showAddDialog(context: Context) {
        val dialogBuilder = AlertDialog.Builder(this)
        val input : EditText=  EditText(this)
        input.hint = "Phrase"
        dialogBuilder.setMessage("Enter Your Secret Phrase")
            .setTitle("Add Secret Message")
            .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, id ->
               if(input.text.trim().isNotEmpty()){
                    if(dbHelper.addPhrase(input.text.toString())>0){
                        Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }else{
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
               }
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            }).setView(input)
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }
}