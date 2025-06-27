package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitaponerileriapp.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Şifre sıfırlama maili gönderme
        binding.sendResetButton.setOnClickListener {
            val email = binding.forgotEmailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Snackbar.make(requireView(), "Lütfen email adresinizi girin.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(requireView(), "Şifre sıfırlama bağlantısı e-postanıza gönderildi. Lütfen spam kutunuzu da kontrol edin!", Snackbar.LENGTH_LONG).show()

                    } else {
                        Snackbar.make(requireView(), "Hata: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        // Giriş ekranına dönüş
        binding.backToLoginText.setOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
