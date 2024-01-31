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
import java.util.Locale

class ItemAdapter(private val context: Context, private val itemList: MutableList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(), Filterable {

    private var filteredList: List<Item> = itemList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.idTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        private val viewIcon: ImageView = itemView.findViewById(R.id.viewIcon)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)

        fun bind(item: Item) {
            idTextView.text = item.id.toString()
            nameTextView.text = item.name.toUpperCase()
            itemView.visibility=View.VISIBLE

            editIcon.setOnClickListener {
                showPopupMenu(item)
            }

            viewIcon.setOnClickListener {
                showView(item)
            }

            deleteIcon.setOnClickListener {
                deleteItem(item)
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
                val query = constraint?.toString()?.toLowerCase(Locale.getDefault()) ?: ""
                filteredList = if (query.isEmpty()) {
                    itemList
                } else {
                    itemList.filter {
                        it.name.toLowerCase(Locale.getDefault()).contains(query)
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                System.out.println("Filtered results: "+ filteredList)
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Item> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount() = itemList.size

    fun isFilteredListEmpty(): Boolean {
        return filteredList.isEmpty()
    }

    private fun showPopupMenu(item: Item) {
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

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(viewItems)

        alertDialogBuilder.setPositiveButton("Done") { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteItem(item: Item) {
        val id = item.id
        val database = FirebaseDatabase.getInstance().reference.child("items")
        val itemRefToDelete: DatabaseReference = database.child("item$id")
        System.out.println(itemRefToDelete)

        itemRefToDelete.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Remove the item from the local list and notify the adapter
                val position = itemList.indexOf(item)
                if (position != -1) {
                    itemList.removeAt(position)
                    notifyItemRemoved(position)
                }
                // Show a toast
                val toast = Toast.makeText(context, "Item has been deleted", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                // Handle the deletion failure
                val toast = Toast.makeText(context, "Failed to deleted", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

}
