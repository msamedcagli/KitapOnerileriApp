package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kitaponerileriapp.adapter.BookAdapter
import com.example.kitaponerileriapp.databinding.FragmentSearchResultsBinding
import com.example.kitaponerileriapp.network.BookRepository
import com.example.kitaponerileriapp.network.RetrofitClient
import kotlinx.coroutines.launch

import androidx.fragment.app.viewModels
import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel

class SearchResultsFragment : Fragment() {

    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookAdapter
    private val repository = BookRepository(RetrofitClient.apiService)
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private val args: SearchResultsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.loadFavoriteBooks() // Fragment tekrar görünür olduğunda favori durumunu yenile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SwipeRefreshLayout dinleyicisi
        binding.searchResultsSwipeRefreshLayout.setOnRefreshListener {
            val searchQuery = args.query.trim()
            fetchSearchResults(searchQuery)
            binding.searchResultsSwipeRefreshLayout.isRefreshing = false
        }

        adapter = BookAdapter(emptyList(), { selectedBook ->
            val action = SearchResultsFragmentDirections.actionSearchResultsFragmentToBookDetailBottomSheet(selectedBook)
            findNavController().navigate(action)
        }, favoritesViewModel)

        favoritesViewModel.favoriteStatusMap.observe(viewLifecycleOwner) {
            adapter.updateBooks(adapter.books, it)
        }

        binding.resultsearchResultsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.resultsearchResultsRecyclerView.adapter = adapter

        binding.resultsearchBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.resultsearchButton.setOnClickListener {
            val query = binding.resultsearchEditText.text.toString().trim()
            if (query.isNotBlank()) {
                fetchSearchResults(query)
            }
        }

        val searchQuery = args.query.trim()
        fetchSearchResults(searchQuery)
    }


    private fun normalizeString(input: String): String {
        return input.lowercase()
            .replace("ş", "s")
            .replace("ç", "c")
            .replace("ö", "o")
            .replace("ü", "u")
            .replace("ğ", "g")
            .replace("ı", "i")
            .trim()
    }

    private fun fetchSearchResults(query: String) {
        lifecycleScope.launch {
            try {
                val allBooks = repository.getAllBooks()
                val normalizedQuery = normalizeString(query)
                val filteredBooks = allBooks.filter {
                    val titleNorm = normalizeString(it.title)
                    val authorNorm = normalizeString(it.author)
                    val categoryNorm = normalizeString(it.category)
                    val matches = titleNorm.contains(normalizedQuery) ||
                            authorNorm.contains(normalizedQuery) ||
                            categoryNorm.contains(normalizedQuery)
                    if (matches) {
                        Log.d("SearchDebug", "Match found: Title='${it.title}', Author='${it.author}', Category='${it.category}'")
                    }
                    matches
                }
                Log.d("SearchDebug", "Query='$query', Matches found=${filteredBooks.size}")
                if (filteredBooks.isEmpty()) {
                    Toast.makeText(requireContext(), "Sonuç bulunamadı", Toast.LENGTH_SHORT).show()
                }
                binding.resultsearchResultsRecyclerView.visibility = View.VISIBLE

                // Ensure favorite status is loaded before updating adapter
                favoritesViewModel.loadFavoriteBooks()
                adapter.updateBooks(filteredBooks, favoritesViewModel.favoriteStatusMap.value ?: emptyMap())
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Arama hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e("SearchDebug", "Arama hatası", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
