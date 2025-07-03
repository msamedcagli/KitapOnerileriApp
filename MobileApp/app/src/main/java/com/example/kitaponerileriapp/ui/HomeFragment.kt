package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitaponerileriapp.MainActivity
import com.example.kitaponerileriapp.R
import com.example.kitaponerileriapp.adapter.BookAdapter
import com.example.kitaponerileriapp.databinding.FragmentHomeBinding
import com.example.kitaponerileriapp.viewmodel.FavoritesViewModel
import com.example.kitaponerileriapp.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private lateinit var popularAdapter: BookAdapter
    private lateinit var newBooksAdapter: BookAdapter
    private lateinit var popularRomanAdapter: BookAdapter

    // 1. RecycledViewPool oluşturuldu. Bu, farklı RecyclerView'ler arasında
    // ViewHolder'ların (görünümlerin) paylaşılmasını sağlar, bu da performansı artırır.
    private val viewPool = RecyclerView.RecycledViewPool()

    // Animasyonlu navOptions tanımı
    private val navOptionsWithAnim = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.loadFavoriteBooks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeSwipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchAllBooks()
            favoritesViewModel.loadFavoriteBooks()
            binding.homeSwipeRefreshLayout.isRefreshing = false
        }

        binding.menuButton.setOnClickListener {
            val drawerLayout = (activity as? MainActivity)?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }

        // 2. Adapter tanımlamaları ListAdapter'a uygun olarak basitleştirildi.
        popularAdapter = BookAdapter({ book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply { putParcelable("book", book) }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }, favoritesViewModel)

        newBooksAdapter = BookAdapter({ book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply { putParcelable("book", book) }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }, favoritesViewModel)

        popularRomanAdapter = BookAdapter({ book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply { putParcelable("book", book) }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }, favoritesViewModel)

        // 3. Favori durumu değişiklikleri dinlenir ve yeni 'updateFavorites' metodu çağrılır.
        favoritesViewModel.favoriteStatusMap.observe(viewLifecycleOwner) { statusMap ->
            popularAdapter.updateFavorites(statusMap)
            newBooksAdapter.updateFavorites(statusMap)
            popularRomanAdapter.updateFavorites(statusMap)
        }

        // 4. RecyclerView ayarları performans optimizasyonları ile güncellendi.
        binding.popularBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
            setRecycledViewPool(viewPool) // Paylaşılan havuzu kullan
            setHasFixedSize(true)       // Performans için boyutun sabit olduğunu belirt
        }

        binding.newBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = newBooksAdapter
            setRecycledViewPool(viewPool) // Paylaşılan havuzu kullan
            setHasFixedSize(true)       // Performans için boyutun sabit olduğunu belirt
        }

        binding.popularRomanRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = popularRomanAdapter
            setRecycledViewPool(viewPool) // Paylaşılan havuzu kullan
            setHasFixedSize(true)       // Performans için boyutun sabit olduğunu belirt
        }

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

        binding.recommendButton.setOnClickListener {
            val bottomSheet = RecommendationFragment()
            bottomSheet.show(parentFragmentManager, "RecommendationBottomSheet")
        }

        // 5. Veri akışları, verimli 'submitList' metodunu kullanacak şekilde güncellendi.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularBooks.collectLatest { books ->
                popularAdapter.submitList(books)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newBooks.collectLatest { books ->
                newBooksAdapter.submitList(books)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularRomanById.collectLatest { books ->
                popularRomanAdapter.submitList(books)
            }
        }

        viewModel.fetchAllBooks()
        favoritesViewModel.loadFavoriteBooks()
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            try {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchResultsFragment(query)
                findNavController().navigate(action, navOptionsWithAnim)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(requireContext(), "Arama sonuçları bulunamadı.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Lütfen bir kitap ismi girin.", Toast.LENGTH_SHORT).show()
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
