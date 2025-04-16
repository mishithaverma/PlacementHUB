package com.android.placementhub.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.placementhub.R
import com.android.placementhub.databinding.FragmentPlacementLoginBinding
import com.android.placementhub.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlacementLoginFragment : Fragment() {

    private var _binding: FragmentPlacementLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacementLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeLoginStatus()
    }
    
    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            attemptLogin()
        }
    }
    
    private fun attemptLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required"
            return
        } else {
            binding.emailInputLayout.error = null
        }
        
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required"
            return
        } else {
            binding.passwordInputLayout.error = null
        }
        
        viewModel.loginPlacementCell(email, password)
    }
    
    private fun observeLoginStatus() {
        viewModel.placementLoginStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    navigateToDashboard()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        // Add progress indicator if needed
    }
    
    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_placementLoginFragment_to_placementDashboardFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 