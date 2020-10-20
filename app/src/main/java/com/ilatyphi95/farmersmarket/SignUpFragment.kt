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
import com.google.firebase.database.FirebaseDatabase
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.databinding.FragmentSignUpBinding
import kotlinx.android.synthetic.main.fragment_sign_up.*


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private var email : String? = null
    private var username : String? = null

    companion object {
        val TAG = "RegisterUser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.shared_transition)

        signUpButton.setOnClickListener {
            SignUpNewUser()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        startFadeInAnimation()
        spanText()
        
        return binding.root
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
                    R.id.action_signUpFragment_to_loginFragment,
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
                ObjectAnimator.ofFloat(binding.fullNameEditText, "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(binding.fullNameTextLayout, "alpha", 0.0f, 1.0f),
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
    private fun SignUpNewUser(){
        username =  fullNameEditText.text.toString()
        email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(email!!.isEmpty() || password.isEmpty() || username!!.isEmpty()){
            Toast.makeText(context, "cannot have empty fields", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "attempting to sign up new user with email $email")

        //Firebase authentication to create a user with email and password

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "successfully created user with uid: ${it.result?.user?.uid}")

                saveUserToDatabase()
            }
            .addOnFailureListener {
                Log.d(TAG, "failed to create user: ${it.message}")
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //save newly created user to database
    private fun saveUserToDatabase(){
        val id = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$id")
        val username = fullNameEditText.text.toString()

        val user = User(id, "", "", email!!, "", "", "", "")

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "user saved to database")
            }
            .addOnFailureListener {
                Log.d(TAG, "${it.message}")
            }
    }
}