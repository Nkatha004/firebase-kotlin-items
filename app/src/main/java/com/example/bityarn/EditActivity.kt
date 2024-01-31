package com.example.bityarn

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

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

        findViewById<Button>(R.id.buttonSave).setOnClickListener{
            val editedName = findViewById<EditText>(R.id.editTextName).text.toString()
            val editedLocation = findViewById<EditText>(R.id.editTextLocation).text.toString()
            val editedType = findViewById<EditText>(R.id.editTextType).text.toString()
            val editedWidth = findViewById<EditText>(R.id.editTextWidth).text.toString().toInt()
            val editedHeight = findViewById<EditText>(R.id.editTextHeight).text.toString().toInt()
            val editedLength = findViewById<EditText>(R.id.editTextLength).text.toString().toInt()

            // Create a map to update the existing item
            val updatedItem = mapOf(
                "id" to id?.toInt(),
                "name" to editedName,
                "location" to editedLocation,
                "type" to editedType,
                "width" to editedWidth,
                "height" to editedHeight,
                "length" to editedLength,
                "status" to spinnerValue.toInt(),
            )

            // Update the existing item in Firebase
            val databaseReference = FirebaseDatabase.getInstance().getReference("items")
            databaseReference.child("item${id}").updateChildren(updatedItem)

            val toast = Toast.makeText(applicationContext, "Update successful", Toast.LENGTH_SHORT)
            toast.show()

            showEditData(updatedItem as Map<String, Any>)
        }
    }

    private fun showEditData(updatedItem:  Map<String, Any>) {
        val inflater = LayoutInflater.from(this)
        val viewItems = inflater.inflate(R.layout.view_form, null)
        val id = updatedItem["id"]
        val name = updatedItem["name"]
        val type = updatedItem["type"]
        val location = updatedItem["location"]
        val length = updatedItem["length"]
        val height = updatedItem["height"]
        val width = updatedItem["width"]
        val status = updatedItem["status"]

        viewItems.findViewById<TextView>(R.id.viewIdText).text="ID: ${id}"
        viewItems.findViewById<TextView>(R.id.viewNameText).text="Name: ${name}"
        viewItems.findViewById<TextView>(R.id.viewTypeText).text="Type: ${type}"
        viewItems.findViewById<TextView>(R.id.viewLocationText).text="Location: ${location}"
        viewItems.findViewById<TextView>(R.id.viewStatusText).text="Status: "+ if(status == 1) "Active" else "Inactive"
        viewItems.findViewById<TextView>(R.id.viewLengthText).text="Length: ${length}"
        viewItems.findViewById<TextView>(R.id.viewWidthText).text="Width: ${width}"
        viewItems.findViewById<TextView>(R.id.viewHeightText).text="Height: ${height}"

        val successDialogBuilder = AlertDialog.Builder(this)
        successDialogBuilder.setMessage("Update successful!")

        // Create the AlertDialog
        val successDialog = successDialogBuilder.create()
        successDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            successDialog.dismiss()

            // Delay for an additional 30 seconds before performing another action
            Handler(Looper.getMainLooper()).postDelayed({
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setView(viewItems)
                alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent)
                }

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

            }, 500)
        }, 1000)
    }
}