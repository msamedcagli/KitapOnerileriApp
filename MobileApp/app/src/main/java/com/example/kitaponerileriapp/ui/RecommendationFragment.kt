package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kitaponerileriapp.databinding.FragmentRecommendationBinding
import com.example.kitaponerileriapp.ui.BookDetailBottomSheet
import com.example.kitaponerileriapp.viewmodel.RecommendationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecommendationFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecommendationViewModel by viewModels()

    private var categories = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kategorileri yükle ve spinner’a yerleştir
        lifecycleScope.launch {
            viewModel.categories.collectLatest {
                categories = it
                val categoryAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories
                )
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.categorySpinner.adapter = categoryAdapter
            }
        }

        binding.submitRecommendation.setOnClickListener {
            val selectedCategory = binding.categorySpinner.selectedItem as? String ?: ""

            if (selectedCategory.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen kategori seçiniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.clearFilteredBooks()
            viewModel.fetchFilteredBooks(selectedCategory)
        }

        lifecycleScope.launch {
            viewModel.filteredBooks.collectLatest { books ->
                if (books.isNotEmpty()) {
                    val firstBook = books.first()

                    val bottomSheet = BookDetailBottomSheet().apply {
                        arguments = Bundle().apply {
                            putParcelable("book", firstBook)
                        }
                    }

                    bottomSheet.show(parentFragmentManager, "BookDetailBottomSheet")
                    viewModel.clearFilteredBooks()
                } else {
                    Toast.makeText(requireContext(), "Bu kategoriye uygun kitap bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest {
                it?.let { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.loadCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
