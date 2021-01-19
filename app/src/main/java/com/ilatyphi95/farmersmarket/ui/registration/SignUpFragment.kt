package com.ilatyphi95.farmersmarket.ui.registration

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.databinding.FragmentSignUpBinding
import com.ilatyphi95.farmersmarket.utils.sendVerificationEmail
import kotlinx.android.synthetic.main.fragment_sign_up.*


class SignUpFragment : Fragment(){
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "RegisterUser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.shared_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        startFadeInAnimation()
        spanText()
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            signUpNewUser()
        }
    }

    private fun spanText() {
        val spannable = SpannableStringBuilder("Already a member? Login")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val extras = FragmentNavigatorExtras(
                    binding.headerContainer to "headerContainer",
                    binding.headerText to "headerText",
                    binding.treeImage to "treeImage"
                )
                findNavController().navigate(
                    R.id.action_signUpFragment2_to_loginFragment2,
                    null,
                    null,
                    extras
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.apply {
                    isUnderlineText = false
                    color = ContextCompat.getColor(activity!!.baseContext, R.color.green_shade_1)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
            }
        }

        spannable.setSpan(
            clickableSpan,
            18,
            binding.loginTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.loginTextView.apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun startFadeInAnimation() {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.createAccountTextView, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.emailTextLayout, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.emailTextLayout, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.passwordEditText, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.passwordLayout, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.confirmPasswordEditText, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.confirmPasswordLayout, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.signUpButton, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.loginTextView, "alpha", 0.0f, 1.0f)
            )
            duration = 500
            start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //sign up new user
    private fun signUpNewUser(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()


        if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Snackbar.make(requireView(),
                getString(R.string.empty_email_password), Snackbar.LENGTH_SHORT).show()
            return
        }

        if(password != confirmPassword) {
            Snackbar.make(requireView(),
                getString(R.string.not_matching_password), Snackbar.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "attempting to sign up new user with email $email")

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Snackbar.make(requireView(),
                    getString(R.string.account_creation_success), Snackbar.LENGTH_SHORT).show()

                Log.d(TAG, "successfully created user with uid: ${it.result?.user?.uid}")

                sendVerificationEmail(requireView())

            }
            .addOnFailureListener {
                Log.d(TAG, "failed to create user: ${it.message}")
                Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
            }
    }

}