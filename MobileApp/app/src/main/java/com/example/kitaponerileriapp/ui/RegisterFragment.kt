package com.example.kitaponerileriapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.kitaponerileriapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.example.kitaponerileriapp.R

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(requireView(), "Lütfen tüm alanları doldurun", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Snackbar.make(requireView(), "Şifre en az 6 karakter olmalı", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Snackbar.make(requireView(), "Doğrulama maili gönderildi. Lütfen spam kutunuzu da kontrol edin!", Snackbar.LENGTH_LONG).show()

                                findNavController().navigate(
                                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(),
                                    navOptions
                                )
                            } else {
                                Snackbar.make(requireView(), "Doğrulama maili gönderilemedi: ${verifyTask.exception?.message}", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Snackbar.make(requireView(), "Kayıt başarısız: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        binding.goToLoginText.setOnClickListener {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(),
                navOptions
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
