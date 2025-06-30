package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.bumptech.glide.Glide
import com.example.kitaponerileriapp.databinding.FragmentBookDetailBinding
import com.example.kitaponerileriapp.model.Book
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.kitaponerileriapp.R
import androidx.fragment.app.viewModels
import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel

class BookDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val book = arguments?.getParcelable<Book>("book")
        if (book != null) {
            Log.d("BookDetailBS", "Book ID: ${book.id}")
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = "Yazar: ${book.author}"
            binding.bookCategory.text = "Kategori: ${book.category}"
            binding.bookSummary.text = if (book.summary.isNullOrEmpty()) {
                "Özet bilgisi bulunmamaktadır."
            } else {
                book.summary
            }

            val imagePath = if (book.image.startsWith("/api")) {
                book.image.removePrefix("/api")
            } else book.image

            val imageUrl = if (!book.image.isNullOrEmpty()) {
                "http://13.48.248.57$imagePath"
            } else null

            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.bookImage)

            // Favori durumu kontrolü ve güncelleme
            favoritesViewModel.favoriteStatusMap.observe(viewLifecycleOwner) { statusMap ->
                val isFavorite = statusMap[book.id.toString()] ?: false
                binding.favoriteIcon.setImageResource(
                    if (isFavorite) R.drawable.favorite else R.drawable.favorite_border
                )
                binding.favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), if (isFavorite) android.R.color.holo_red_dark else android.R.color.black))
            }
            favoritesViewModel.checkFavoriteStatus(book.id.toString())

            // Favori iconuna tıklama
            binding.favoriteIcon.setOnClickListener {
                Log.d("BookDetailBS", "Favorite icon clicked for book ID: ${book.id}")
                favoritesViewModel.toggleFavorite(book.id.toString())
            }
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
