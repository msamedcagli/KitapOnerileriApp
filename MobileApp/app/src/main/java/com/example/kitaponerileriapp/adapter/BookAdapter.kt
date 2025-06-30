package com.example.kitaponerileriapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kitaponerileriapp.R
import com.example.kitaponerileriapp.databinding.ItemBookCardBinding
import com.example.kitaponerileriapp.model.Book
import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel
import com.bumptech.glide.Glide
import androidx.core.content.ContextCompat

class BookAdapter(
    var books: List<Book>,
    private val onItemClick: (Book) -> Unit,
    private val favoritesViewModel: FavoritesViewModel,
    private var currentFavoriteStatus: Map<String, Boolean> = emptyMap()
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author

            val imagePath = if (book.image.startsWith("/api")) {
                book.image.removePrefix("/api")
            } else book.image

            Glide.with(itemView.context)
                .load("http://13.48.248.57$imagePath")
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.bookImage)

            // Favori durumu kontrolü
            val isFavorite = currentFavoriteStatus[book.id.toString()] ?: false
            binding.favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.favorite else R.drawable.favorite_border
            )
            binding.favoriteIcon.setColorFilter(ContextCompat.getColor(itemView.context, if (isFavorite) android.R.color.holo_red_dark else android.R.color.black))

            // Favori iconuna tıklama
            binding.favoriteIcon.setOnClickListener {
                favoritesViewModel.toggleFavorite(book.id.toString())
            }

            binding.root.setOnClickListener {
                onItemClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    fun updateBooks(newBooks: List<Book>, newFavoriteStatus: Map<String, Boolean>) {
        books = newBooks
        currentFavoriteStatus = newFavoriteStatus
        notifyDataSetChanged()
    }
}
