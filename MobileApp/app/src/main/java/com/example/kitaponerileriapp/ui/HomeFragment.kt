package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kitaponerileriapp.adapter.BookAdapter
import com.example.kitaponerileriapp.viewmodel.HomeViewModel
import com.example.kitaponerileriapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.kitaponerileriapp.ui.BookDetailBottomSheet

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var popularAdapter: BookAdapter
    private lateinit var newBooksAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popularAdapter = BookAdapter(emptyList()) { book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable("book", book)
                }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }
        newBooksAdapter = BookAdapter(emptyList()) { book ->
            val bottomSheet = BookDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable("book", book)
                }
            }
            bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
        }

        binding.popularBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }

        binding.newBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
            adapter = newBooksAdapter
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularBooks.collectLatest { books ->
                popularAdapter.updateBooks(books)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newBooks.collectLatest { books ->
                newBooksAdapter.updateBooks(books)
            }
        }


        viewModel.fetchAllBooks()
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchResultsFragment(query)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Lütfen bir kitap ismi girin.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
