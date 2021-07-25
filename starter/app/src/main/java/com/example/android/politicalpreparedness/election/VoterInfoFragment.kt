package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.WebViewActivity

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        val dataSource = ElectionDatabase.getInstance(requireContext().applicationContext).electionDao

        var args = VoterInfoFragmentArgs.fromBundle(requireArguments())

        val viewModel = ViewModelProvider(this, VoterInfoViewModelFactory(dataSource, args.argDivision, args.argElectionId)).get(VoterInfoViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //TODO: Handle save button UI state
         viewModel.isSaved.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.followBtn.text = resources.getText(R.string.unfollow_button)
            else
                binding.followBtn.text = resources.getText(R.string.follow_button)
        })

        binding.followBtn.setOnClickListener {
            if(viewModel.isSaved.value!!)
                viewModel.removeElection()
            else
                viewModel.saveElection()
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            when (it){
                ApiStatus.LOADING -> {}
                ApiStatus.DONE -> {}
                ApiStatus.ERROR -> {
                    findNavController().navigate(VoterInfoFragmentDirections.actionVoterInfoFragmentToErrorFragment())
                    viewModel.resetStatus()
                }
                else -> {}
            }
        })

        binding.stateLocations.setOnClickListener {
            viewModel.location.value?.let{loadWebView(it)}
        }

        binding.stateBallot.setOnClickListener {
            viewModel.ballot.value?.let{loadWebView(it)}
        }

        return binding.root

        //TODO: Add ViewModel values and create ViewModel


        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: cont'd Handle save button clicks

    }
    //TODO: Create method to load URL intents

    fun loadWebView(url: String){
        val intent = Intent(requireActivity(), WebViewActivity::class.java).apply {
           putExtra ("URL", url)
        }
        startActivity(intent)
    }



}