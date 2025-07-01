package com.example.kitaponerileriapp.ui

import com.example.kitaponerileriapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kitaponerileriapp.MainActivity
import com.example.kitaponerileriapp.adapter.BookAdapter
import com.example.kitaponerileriapp.databinding.FragmentHomeBinding
import com.example.kitaponerileriapp.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private lateinit var popularAdapter: BookAdapter
    private lateinit var newBooksAdapter: BookAdapter
    private lateinit var popularRomanAdapter: BookAdapter

    // Animasyonlu navOptions tanımı
    private val navOptionsWithAnim = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.loadFavoriteBooks() // Fragment tekrar görünür olduğunda favori durumunu yenile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SwipeRefreshLayout dinleyicisi
        binding.homeSwipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchAllBooks()
            favoritesViewModel.loadFavoriteBooks() // Favori durumunu da yenile
            binding.homeSwipeRefreshLayout.isRefreshing = false
        }

        // Menü butonuna tıklanınca drawer'ı aç
        binding.menuButton.setOnClickListener {
            val drawerLayout = (activity as? MainActivity)?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }

        // Adapter tanımlamaları
        popularAdapter = BookAdapter(emptyList(), { book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable("book", book)
                }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }, favoritesViewModel)

        newBooksAdapter = BookAdapter(emptyList(), { book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable("book", book)
                }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }, favoritesViewModel)

        popularRomanAdapter = BookAdapter(emptyList(), { book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable("book", book)
                }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }, favoritesViewModel)

        // Favori durumu değişikliklerini dinle ve adaptörleri güncelle
        favoritesViewModel.favoriteStatusMap.observe(viewLifecycleOwner) {
            popularAdapter.updateBooks(popularAdapter.books, it)
            newBooksAdapter.updateBooks(newBooksAdapter.books, it)
            popularRomanAdapter.updateBooks(popularRomanAdapter.books, it)
        }

        // RecyclerView ayarları
        binding.popularBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }

        binding.newBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = newBooksAdapter
        }

        binding.popularRomanRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = popularRomanAdapter
        }

        // Arama işlemi
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.searchButton.setOnClickListener {
            performSearch()
        }

        // Kitap öner butonu
        binding.recommendButton.setOnClickListener {
            val bottomSheet = RecommendationFragment()
            bottomSheet.show(parentFragmentManager, "RecommendationBottomSheet")
        }

        // Veri akışları
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularBooks.collectLatest { books ->
                popularAdapter.updateBooks(books, favoritesViewModel.favoriteStatusMap.value ?: emptyMap())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newBooks.collectLatest { books ->
                newBooksAdapter.updateBooks(books, favoritesViewModel.favoriteStatusMap.value ?: emptyMap())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularRomanById.collectLatest { books ->
                popularRomanAdapter.updateBooks(books, favoritesViewModel.favoriteStatusMap.value ?: emptyMap())
            }
        }

        viewModel.fetchAllBooks()
        favoritesViewModel.loadFavoriteBooks() // Uygulama açıldığında favori durumunu yükle
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchResultsFragment(query)
            findNavController().navigate(action, navOptionsWithAnim)
        } else {
            Toast.makeText(requireContext(), "Lütfen bir kitap ismi girin.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
