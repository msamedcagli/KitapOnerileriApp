package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kitaponerileriapp.adapter.BookAdapter
import com.example.kitaponerileriapp.databinding.FragmentSearchResultsBinding
import com.example.kitaponerileriapp.network.BookRepository
import com.example.kitaponerileriapp.network.RetrofitClient
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels
import com.example.kitaponerileriapp.model.Book
import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel
import com.example.kitaponerileriapp.R

class SearchResultsFragment : Fragment() {

    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookAdapter
    private val repository = BookRepository(RetrofitClient.apiService)
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private val args: SearchResultsFragmentArgs by navArgs()

    private val navOptions = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.loadFavoriteBooks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()

        binding.searchResultsSwipeRefreshLayout.setOnRefreshListener {
            val searchQuery = args.query.trim()
            fetchSearchResults(searchQuery)
            binding.searchResultsSwipeRefreshLayout.isRefreshing = false
        }

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
        if(searchQuery.isNotEmpty()){
            binding.resultsearchEditText.setText(searchQuery)
            fetchSearchResults(searchQuery)
        }
    }

    private fun setupRecyclerView() {
        // 1. Adapter oluşturma işlemi ListAdapter'a uygun olarak güncellendi.
        adapter = BookAdapter({ selectedBook ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply { putParcelable("book", selectedBook) }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }, favoritesViewModel)

        binding.resultsearchResultsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.resultsearchResultsRecyclerView.adapter = adapter
        binding.resultsearchResultsRecyclerView.setHasFixedSize(true) // Performans optimizasyonu
    }

    private fun observeFavorites() {
        // 2. Favori durumu değişiklikleri dinlenir ve adaptör güncellenir.
        favoritesViewModel.favoriteStatusMap.observe(viewLifecycleOwner) { statusMap ->
            adapter.updateFavorites(statusMap)
        }
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
                    titleNorm.contains(normalizedQuery) ||
                            authorNorm.contains(normalizedQuery) ||
                            categoryNorm.contains(normalizedQuery)
                }

                if (filteredBooks.isEmpty()) {
                    Toast.makeText(requireContext(), "Sonuç bulunamadı", Toast.LENGTH_SHORT).show()
                }
                binding.resultsearchResultsRecyclerView.visibility = View.VISIBLE

                // 3. Arama sonuçları verimli 'submitList' metodu ile adaptöre gönderilir.
                adapter.submitList(filteredBooks)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Arama hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e("SearchDebug", "Arama hatası", e)
            }
        }
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
