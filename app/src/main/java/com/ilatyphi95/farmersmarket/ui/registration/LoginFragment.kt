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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.repository.FirebaseMessagingService
import com.ilatyphi95.farmersmarket.databinding.FragmentLoginBinding
import com.ilatyphi95.farmersmarket.utils.sendVerificationEmail
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment(){
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object{
        const val TAG = "LOGIN"
        const val languageCode = "en"
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        startFadeInAnimation()
        spanText()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            loginUser()
        }

        binding.forgotPasswordTextView.setOnClickListener{
            resetPassword()
        }
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
                    R.id.action_loginFragment2_to_signUpFragment2,
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
            .addOnSuccessListener {
                if(FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {

                    // register device for fcm messaging
                    FirebaseMessagingService.useToken {
                        FirebaseMessagingService.sendRegistrationToServer(it)
                    }

                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragment2ToNavigationHome()
                    )
                } else {
                    sendVerificationEmail(requireView())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "${it.message}")
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //reset password
    private fun resetPassword(){
        val email = emailEditText.text.toString()

        if(email.isEmpty()){
            Toast.makeText(context, "email field cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().setLanguageCode(languageCode)
        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnCompleteListener {
                //receives response from firebase
            }
            .addOnSuccessListener {
                Log.d(TAG, "email sent successfully to $email")
                Toast.makeText(context, "password reset email sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "${it.message}")
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }

        FirebaseAuth.getInstance().signOut()
    }
}