package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.election.models.Election
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.asDomainModel
import kotlinx.coroutines.launch
import java.lang.Exception

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val dataSource: ElectionDao): ViewModel() {

    //TODO: Create live data val for upcoming elections
    val upcoming_elections = Transformations.map(dataSource.getAllUpcomingElections()){
        it.asDomainModel()
    }

    val saved_elections = Transformations.map(dataSource.getAllSavedElections()){
        it.asDomainModel()
    }

    private val _navigateToElection = MutableLiveData<Election>()
    val navigateToElection : LiveData<Election>
        get() = _navigateToElection

    init {
         getUpcomingElections()
    }



    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun getUpcomingElections(){
        viewModelScope.launch {
            try {
                var listOfElections = CivicsApi.retrofitService.getElectionQuery().elections

                if (listOfElections.isNotEmpty()) {
                        dataSource.insertAllUpcomingElections(*listOfElections.toTypedArray())
                }
            }catch (e: Exception){
                println("EXCEPTION: ${e.message}")
            }
        }
    }


    //TODO: Create functions to navigate to saved or upcoming election voter info
    fun onElectionClicked(election: Election){
        _navigateToElection.value = election
    }

    fun clearNavigationDestination() {
        _navigateToElection.value = null
    }


}