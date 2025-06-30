package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kitaponerileriapp.adapter.BookAdapter
import com.example.kitaponerileriapp.databinding.FragmentFavoritesBinding
import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel
import com.example.kitaponerileriapp.R


class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SwipeRefreshLayout dinleyicisi
        binding.favoritesSwipeRefreshLayout.setOnRefreshListener {
            favoritesViewModel.loadFavoriteBooks()
            binding.favoritesSwipeRefreshLayout.isRefreshing = false
        }

        setupRecyclerView()
        observeViewModel()

        favoritesViewModel.loadFavoriteBooks()

        binding.favoritesBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_favoritesFragment_to_homeFragment)
        }

    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            books = emptyList(),
            onItemClick = { book ->
                val bottomSheet = BookDetailBottomSheet().apply {
                    arguments = Bundle().apply {
                        putParcelable("book", book)
                    }
                }
                bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
            },
            favoritesViewModel = favoritesViewModel
        )

        binding.favoritesRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = bookAdapter
        }
    }

    private fun observeViewModel() {
        favoritesViewModel.favoriteBooks.observe(viewLifecycleOwner) { books ->
            bookAdapter.updateBooks(books, favoritesViewModel.favoriteStatusMap.value ?: emptyMap())
        }

        favoritesViewModel.favoriteStatusMap.observe(viewLifecycleOwner) {
            bookAdapter.updateBooks(favoritesViewModel.favoriteBooks.value ?: emptyList(), it)
        }

        // Favoriler sayfasında favori durumu değiştiğinde listeyi yeniden yükle
        favoritesViewModel.favoriteStatusMap.observe(viewLifecycleOwner) {
            favoritesViewModel.loadFavoriteBooks()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
