package com.example.android.politicalpreparedness.election

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentErrorBinding

class ErrorFragment : Fragment() {


    private lateinit var binding: FragmentErrorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_error, container, false)

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

}