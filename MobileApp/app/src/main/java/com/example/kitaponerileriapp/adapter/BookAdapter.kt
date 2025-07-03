package com.example.kitaponerileriapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kitaponerileriapp.R
import com.example.kitaponerileriapp.databinding.ItemBookCardBinding
import com.example.kitaponerileriapp.model.Book
import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel

// 1. DiffUtil.ItemCallback oluşturuldu. Bu, ListAdapter'ın iki liste arasındaki farkı
// verimli bir şekilde hesaplamasını sağlar.
class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        // Öğelerin benzersiz ID'lerini karşılaştırır.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        // Öğelerin içeriğini karşılaştırır. Data class'lar bunu otomatik yapar.
        return oldItem == newItem
    }
}

// 2. Adapter, RecyclerView.Adapter yerine ListAdapter'dan miras alacak şekilde güncellendi.
class BookAdapter(
    private val onItemClick: (Book) -> Unit,
    private val favoritesViewModel: FavoritesViewModel
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    // Favori durumlarını tutmak için ayrı bir değişken.
    private var favoriteStatusMap: Map<String, Boolean> = emptyMap()

    inner class BookViewHolder(val binding: ItemBookCardBinding) : RecyclerView.ViewHolder(binding.root) {
        // 3. bind metodu, artık favori durumunu dışarıdan alıyor.
        fun bind(book: Book, isFavorite: Boolean) {
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author

            val imagePath = if (book.image.startsWith("/api")) {
                book.image.removePrefix("/api")
            } else book.image

            Glide.with(itemView.context)
                .load("http://13.48.248.57$imagePath")
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.bookImage)

            // Favori durumu doğrudan parametreden ayarlanıyor.
            binding.favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.favorite else R.drawable.favorite_border
            )
            binding.favoriteIcon.setColorFilter(ContextCompat.getColor(itemView.context, if (isFavorite) android.R.color.holo_red_dark else android.R.color.black))

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
        // 4. getItem() metodu ile mevcut öğe alınır ve favori durumu hesaplanır.
        val book = getItem(position)
        val isFavorite = favoriteStatusMap[book.id.toString()] ?: false
        holder.bind(book, isFavorite)
    }

    // 5. Yeni metod: Sadece favori durumlarını günceller ve ekranı yeniler.
    // Bu, tüm listeyi değiştirmekten daha az maliyetlidir, ancak hala notifyDataSetChanged kullanır.
    // Liste güncellemeleri artık submitList ile çok daha verimli.
    fun updateFavorites(newFavoriteStatus: Map<String, Boolean>) {
        favoriteStatusMap = newFavoriteStatus
        notifyDataSetChanged() // Sadece favori ikonlarının durumunu yenilemek için.
    }
}
