package com.example.bityarn

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        var itemList: MutableList<Item> = mutableListOf()
        var itemAdapter = ItemAdapter(this, itemList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        // Initialize Firebase
        var database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("items")

        // fetch data from Firebase
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

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // Add the ValueEventListener to the database reference
        database.addValueEventListener(valueEventListener)

        // Initialize SearchView
        var searchView: SearchView = findViewById(R.id.searchView)
        searchView.queryHint="Search by name..."

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

