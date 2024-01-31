package com.example.bityarn

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemList: MutableList<Item>
    private lateinit var database: DatabaseReference
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemList = mutableListOf()
        itemAdapter = ItemAdapter(this, itemList)
        recyclerView.adapter = itemAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference.child("items")

        // Set up ValueEventListener to fetch data from Firebase
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                itemList.clear()

                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(Item::class.java)
                    item?.let {
                        itemList.add(it)
                    }
                }

                // Notify the adapter that data has changed
                itemAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        }

        // Add the ValueEventListener to the database reference
        database.addValueEventListener(valueEventListener)

        // Initialize SearchView
        searchView = findViewById(R.id.searchView)
        searchView.queryHint = "Search by name..." // Set the placeholder/hint

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemAdapter.filter.filter(newText)
                return true
            }
        })
    }
}

