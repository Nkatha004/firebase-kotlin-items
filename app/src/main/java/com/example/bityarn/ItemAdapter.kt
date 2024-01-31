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
import java.util.Locale

class ItemAdapter(private val context: Context, private val itemList: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(), Filterable {

    private var filteredList: List<Item> = itemList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.idTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        private val viewIcon: ImageView = itemView.findViewById(R.id.viewIcon)

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
}
