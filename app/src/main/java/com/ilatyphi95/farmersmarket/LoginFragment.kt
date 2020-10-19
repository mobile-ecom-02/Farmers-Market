package com.ilatyphi95.farmersmarket

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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object{
        val TAG = "LOGIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.shared_transition)

        loginButton.setOnClickListener {
            loginUser()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        startFadeInAnimation()
        spanText()

        return binding.root
    }


    private fun spanText() {
        val spannable = SpannableStringBuilder("Not a member? Sign Up")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val extras = FragmentNavigatorExtras(
                    binding.headerContainer to "headerContainer",
                    binding.headerText to "headerText",
                    binding.treeImage to "treeImage"
                )
                findNavController().navigate(
                    R.id.action_loginFragment_to_signUpFragment2,
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
            14,
            21,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.signUpTextView.apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun startFadeInAnimation() {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.welcomeTextView, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.emailTextLayout, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.emailTextLayout, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.passwordEditText, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.passwordLayout, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.loginButton, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.signUpTextView, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.forgotPasswordTextView, "alpha", 0.0f, 1.0f)
            )
            duration = 500
            start()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //login user
    private fun loginUser(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(context, "cannot have empty fields", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { 
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "logged in user ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Log.d(TAG, "${it.message}")
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}