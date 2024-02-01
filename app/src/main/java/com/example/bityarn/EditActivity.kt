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

        //Get the details of the item being passed
        val item = intent.getStringExtra("item") //Item(id=10, name=name 10, location=location 10, type=type 10, width=1000, height=1000, length=1000, status=0)

        val keyValuePairs = item
            ?.substringAfter("(") // Remove "Item("
            ?.substringBeforeLast(")") // Remove the trailing ")"
            ?.split(", ") // Split by ", "

        val itemMap = keyValuePairs
            ?.associate {
                val (key, value) = it.split("=")
                key.trim() to value.trim()
            }

        // Create a spinner (select dropdown) with values(active and inactive)
        val spinner: Spinner = findViewById(R.id.spinner)
        val items = listOf("Active", "Inactive")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set a default value for the status field that already exists (active or inactive)
        var spinnerValue = ""
        if(itemMap?.getValue("status") == "1") spinnerValue = "Active" else spinnerValue = "Inactive"
        val defaultIndex = items.indexOf(spinnerValue)
        spinner.setSelection(defaultIndex)

        // populate the input fields with details specific to selected item
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
                if (selectedItem == "Active") spinnerValue = "1" else spinnerValue = "0"

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //save the edits to firebase on button click
        findViewById<Button>(R.id.buttonSave).setOnClickListener{

            //get the ID of the item being updated
            val id = itemMap?.getValue("id")

            // Create a map to update the existing item
            val updatedItem = mapOf(
                "id" to id?.toInt(),
                "name" to findViewById<EditText>(R.id.editTextName).text.toString(),
                "location" to findViewById<EditText>(R.id.editTextLocation).text.toString(),
                "type" to findViewById<EditText>(R.id.editTextType).text.toString(),
                "width" to findViewById<EditText>(R.id.editTextWidth).text.toString().toInt(),
                "height" to findViewById<EditText>(R.id.editTextHeight).text.toString().toInt(),
                "length" to findViewById<EditText>(R.id.editTextLength).text.toString().toInt(),
                "status" to spinnerValue.toInt(),
            )

            // Update the existing item in Firebase
            val databaseReference = FirebaseDatabase.getInstance().getReference("items")
            databaseReference.child("item${id}").updateChildren(updatedItem)

            //update successful
            val toast = Toast.makeText(applicationContext, "Update successful", Toast.LENGTH_SHORT)
            toast.show()

            //display a card view with the updated data
            showEditData(updatedItem as Map<String, Any>)
        }
    }

    private fun showEditData(updatedItem:  Map<String, Any>) {

        //show a successful update popup
        val successDialogBuilder = AlertDialog.Builder(this)
        successDialogBuilder.setMessage("Update successful!")
        val successDialog = successDialogBuilder.create()
        successDialog.show()

        // populate the view card for showing the updated item
        val viewItems = LayoutInflater.from(this).inflate(R.layout.view_form, null)
        viewItems.findViewById<TextView>(R.id.viewIdText).text="ID: " + updatedItem["id"]
        viewItems.findViewById<TextView>(R.id.viewNameText).text="Name: "+ updatedItem["name"]
        viewItems.findViewById<TextView>(R.id.viewTypeText).text="Type: " + updatedItem["type"]
        viewItems.findViewById<TextView>(R.id.viewLocationText).text="Location: " + updatedItem["location"]
        viewItems.findViewById<TextView>(R.id.viewStatusText).text="Status: "+ if(updatedItem["status"] == 1) "Active" else "Inactive"
        viewItems.findViewById<TextView>(R.id.viewLengthText).text="Length: " + updatedItem["length"]
        viewItems.findViewById<TextView>(R.id.viewWidthText).text="Width: " + updatedItem["width"]
        viewItems.findViewById<TextView>(R.id.viewHeightText).text="Height: " + updatedItem["height"]

        Handler(Looper.getMainLooper()).postDelayed({
            successDialog.dismiss()

            // Delay before showing the view of the updated item
            Handler(Looper.getMainLooper()).postDelayed({

                //show the updated item on a view card
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setView(viewItems)

                //redirect back to the home page with all the items
                alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent)
                }
                alertDialogBuilder.create().show()

            }, 500) // delay for 0.5 seconds after the success popup and show the card view of the updated item
        }, 1000) // show update success for 1 second
    }
}