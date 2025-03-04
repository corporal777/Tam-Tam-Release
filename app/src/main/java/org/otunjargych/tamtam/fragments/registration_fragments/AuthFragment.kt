package org.otunjargych.tamtam.fragments.registration_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.activities.MainActivity
import org.otunjargych.tamtam.databinding.FragmentAuthBinding
import org.otunjargych.tamtam.extensions.AUTH
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.extensions.hasConnection
import org.otunjargych.tamtam.extensions.toastMessage

class AuthFragment() : BaseFragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.authToolbar.customTitle.text = getString(R.string.authorization)

        Boom(binding.btnEnter)
        binding.btnEnter.setOnClickListener {
            if (hasConnection(requireContext())){
                binding.progressView.isVisible = true
                authUser()
            }else{
                toastMessage(requireContext(), getString(R.string.no_connection))
            }

        }

        binding.tvRegistration.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.registration_container, RegistrationFragment()).commit()
        }

        return binding.root
    }


    private fun authUser() {

        val phoneNumber: String = binding.etNumberPhoneAuth.text.toString() + "@gmail.com"
        val password: String = binding.etPasswordAuth.text.toString()

        AUTH = FirebaseAuth.getInstance()
        if (AUTH.currentUser == null) {
            if (!phoneNumber.isNullOrEmpty() && !password.isNullOrEmpty()) {
                AUTH.signInWithEmailAndPassword(phoneNumber, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.progressView.isVisible = false
                        toastMessage(requireContext(), "Добро пожаловать")
                        replaceActivity()
                    }
                }.addOnFailureListener {
                    binding.progressView.isVisible = false
                    toastMessage(requireContext(), "Такого аккаунта не существует!")
                }
            } else toastMessage(requireContext(), "Заполните все поля ввода!")
        } else {
            toastMessage(requireContext(), "Вы уже авторизованы")
            replaceActivity()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
//        requireActivity().overridePendingTransition(
//            android.R.anim.fade_in,
//            android.R.anim.fade_out
//        )
    }

}