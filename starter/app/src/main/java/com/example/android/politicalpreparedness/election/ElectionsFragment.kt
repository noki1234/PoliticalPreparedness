package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)

        val dataSource = ElectionDatabase.getInstance(requireContext().applicationContext).electionDao

        val viewModel = ViewModelProvider(this, ElectionsViewModelFactory(dataSource)).get(ElectionsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        //TODO: Initiate recycler adapters
        val electionListAdapter = ElectionListAdapter(ElectionListener {
            election -> viewModel.onElectionClicked(election)
        })

        val savedElectionListAdapter = ElectionListAdapter(ElectionListener {
            election -> viewModel.onElectionClicked(election)
        })
        binding.upcomingElectionsRecyclerView.adapter = electionListAdapter
        binding.savedElectionsRecyclerView.adapter = savedElectionListAdapter



        //TODO: Populate recycler adapters
        viewModel.upcoming_elections.observe(viewLifecycleOwner, Observer {
            it?.let {
                electionListAdapter.submitList(it)
            }
        })

        viewModel.saved_elections.observe(viewLifecycleOwner, Observer {
            it?.let {
                savedElectionListAdapter.submitList(it)
            }
        })


        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Link elections to voter info
        viewModel.navigateToElection.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                viewModel.clearNavigationDestination()
            }
        })

        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

}