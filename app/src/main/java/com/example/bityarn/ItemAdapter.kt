package com.example.bityarn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ItemAdapter(private val context: Context, private val itemList: MutableList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(), Filterable {

    private var filteredList: List<Item> = itemList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.idTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        private val viewIcon: ImageView = itemView.findViewById(R.id.viewIcon)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)

        fun bind(item: Item) {
            idTextView.text = item.id.toString()
            nameTextView.text = item.name.uppercase()

            // Set the visibility of the views based on whether the item is filtered
            if (isFilteredListEmpty()) {
                itemView.visibility = View.GONE
            } else {
                itemView.visibility = View.VISIBLE

                editIcon.setOnClickListener {
                    editItem(item)
                }

                viewIcon.setOnClickListener {
                    showView(item)
                }

                deleteIcon.setOnClickListener {
                    deleteItem(item)
                }
            }
        }

        fun bindEmptyState() {
            itemView.visibility=View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isFilteredListEmpty() || position >= filteredList.size) {
            holder.bindEmptyState()
        }else {
            val currentItem = filteredList[position]
            holder.bind(currentItem)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""
                filteredList = if (query.isEmpty()) {
                    itemList
                } else {
                    itemList.filter {
                        it.name.lowercase().contains(query)
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                System.out.println("Filtered results: "+ filteredList)
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as? List<Item>) ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount() = itemList.size

    private fun isFilteredListEmpty(): Boolean {
        return filteredList.isEmpty()
    }

    private fun editItem(item: Item) {
        val intent = Intent(context, EditActivity::class.java)
        intent.putExtra("item", "$item")
        context.startActivity(intent)
    }

    private fun showView(item: Item) {
        val inflater = LayoutInflater.from(context)
        val viewItems = inflater.inflate(R.layout.view_form, null)

        viewItems.findViewById<TextView>(R.id.viewIdText).text="ID: ${item.id}"
        viewItems.findViewById<TextView>(R.id.viewNameText).text="Name: ${item.name}"
        viewItems.findViewById<TextView>(R.id.viewTypeText).text="Type: ${item.type}"
        viewItems.findViewById<TextView>(R.id.viewLocationText).text="Location: ${item.location}"
        viewItems.findViewById<TextView>(R.id.viewStatusText).text="Status: "+ if(item.status == 1) "Active" else "Inactive"
        viewItems.findViewById<TextView>(R.id.viewLengthText).text="Length: ${item.length}"
        viewItems.findViewById<TextView>(R.id.viewWidthText).text="Width: ${item.width}"
        viewItems.findViewById<TextView>(R.id.viewHeightText).text="Height: ${item.height}"

        // show a card view with all details for the selected item
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(viewItems)
        alertDialogBuilder.setPositiveButton("Done") { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.create().show()
    }

    private fun deleteItem(item: Item) {
        val id = item.id
        val database = FirebaseDatabase.getInstance().reference.child("items")
        val itemRefToDelete: DatabaseReference = database.child("item$id")

        //removes the item from db
        itemRefToDelete.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Remove the item from the local list and notify the adapter to modify the interface alignment
                val position = itemList.indexOf(item)
                if (position != -1) {
                    itemList.removeAt(position)
                    notifyItemRemoved(position)
                }

                //deletion successful
                val toast = Toast.makeText(context, "Item has been deleted", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                // deletion failed
                val toast = Toast.makeText(context, "Failed to deleted", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

}
