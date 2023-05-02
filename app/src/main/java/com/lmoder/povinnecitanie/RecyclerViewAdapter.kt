package com.lmoder.povinnecitanie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load

class RecyclerViewAdapter(private val mBooks: ArrayList<Book>, val context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView
        val authorTextView: TextView
        val bookImage: ImageView
        val bookItem: ConstraintLayout

        init {
            // Define click listener for the ViewHolder's View
            nameTextView = view.findViewById(R.id.bookNameTextView)
            authorTextView = view.findViewById(R.id.bookAuthorTextView)
            bookImage = view.findViewById(R.id.bookImageView)
            bookItem = view.findViewById(R.id.bookItemLayout)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.nameTextView.text = mBooks[position].name
        viewHolder.authorTextView.text = mBooks[position].author
        viewHolder.bookImage.load(mBooks[position].imageURL){
            placeholder(R.drawable.book)
            error(R.drawable.book)
        }

        viewHolder.bookItem.setOnClickListener{
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("name", mBooks[position].name)
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = mBooks.size

}