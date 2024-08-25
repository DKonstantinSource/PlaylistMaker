package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var resetButton: Button
    var searchQuery: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText(searchQuery)
        resetButton = findViewById<Button>(R.id.reset_button)

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchEditText.postDelayed({
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
                }, 200)
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resetButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchQuery = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        resetButton.setOnClickListener {
            searchEditText.text.clear()
            searchQuery ="" //
            searchEditText.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            resetButton.visibility = View.GONE //
        }


        var backOnMainActivity = findViewById<ImageView>(R.id.backButton)
        backOnMainActivity.setOnClickListener { finish()}




    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_query", searchQuery) // Сохраняем текст из EditText в Bundle
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString("search_query", "") // Извлекаем текст из Bundle
        searchEditText.setText(searchQuery) // Устанавливаем восстановленное значение в EditText
    }

}