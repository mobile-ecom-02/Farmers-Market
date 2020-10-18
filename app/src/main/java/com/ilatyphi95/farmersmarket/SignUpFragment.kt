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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.ilatyphi95.farmersmarket.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.shared_transition)
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

}