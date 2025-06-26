package com.example.kitaponerileriapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kitaponerileriapp.R
import com.example.kitaponerileriapp.model.Book

class BookAdapter(
    private var books: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)

        private var isFavorite = false  // Geçici toggle durumu

        fun bind(book: Book) {
            Log.d("BookAdapter", "title: '${book.title}', author: '${book.author}'")

            bookTitle.text = book.title ?: "Bilinmeyen Kitap"
            bookAuthor.text = book.author ?: "Bilinmeyen Yazar"

            val imagePath = if (book.image.startsWith("/api")) {
                book.image.removePrefix("/api")
            } else book.image

            Glide.with(itemView.context)
                .load("http://13.48.248.57$imagePath")
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(bookImage)

            itemView.setOnClickListener {
                onItemClick(book)
            }

            // Kalp ikonuna tıklanınca toggle et
            favoriteIcon.setOnClickListener {
                isFavorite = !isFavorite

                if (isFavorite) {
                    favoriteIcon.setImageResource(R.drawable.favorite)
                    favoriteIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                } else {
                    favoriteIcon.setImageResource(R.drawable.favorite_border)
                    favoriteIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.black))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_card, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}
