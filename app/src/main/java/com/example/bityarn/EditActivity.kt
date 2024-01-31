package com.example.bityarn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        val item = intent.getStringExtra("item")

        val keyValuePairs = item
            ?.substringAfter("(") // Remove "Item("
            ?.substringBeforeLast(")") // Remove the trailing ")"
            ?.split(", ") // Split by ", "

        val itemMap = keyValuePairs
            ?.associate {
                val (key, value) = it.split("=")
                key.trim() to value.trim()
            }

        val id = itemMap?.getValue("id")
        val spinner: Spinner = findViewById(R.id.spinner)
        val items = listOf("Active", "Inactive")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Set a default selected value
        var spinnerValue = ""
        if(itemMap?.getValue("status") == "1") spinnerValue = "Active" else spinnerValue = "Inactive"
        val defaultIndex = items.indexOf(spinnerValue)
        spinner.setSelection(defaultIndex)

        findViewById<EditText>(R.id.editTextName).setText(itemMap?.getValue("name"))
        findViewById<EditText>(R.id.editTextType).setText(itemMap?.getValue("type"))
        findViewById<EditText>(R.id.editTextLocation).setText(itemMap?.getValue("location"))
        findViewById<EditText>(R.id.editTextLength).setText(itemMap?.getValue("length"))
        findViewById<EditText>(R.id.editTextWidth).setText(itemMap?.getValue("width"))
        findViewById<EditText>(R.id.editTextHeight).setText(itemMap?.getValue("height"))


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = items[position]
                // Handle the selected item
                // For example, you can use selectedItem to determine if it's "Active" or "Inactive"
                when (selectedItem) {
                    "Active" -> {
                        spinnerValue = "1"
                    }

                    "Inactive" -> {
                        spinnerValue = "0"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
        }
    }
}