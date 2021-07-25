package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.SavedElectionEntity
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*


class VoterInfoViewModel(private val dataSource: ElectionDao, private val electionDivision: Division, private val electionId: Int) : ViewModel() {

    //TODO: Add live data to hold voter info
    init {
        getVoterInfo()
        setButtonStatus()
    }

    private val _status = MutableLiveData<ApiStatus>()
        val status: LiveData<ApiStatus>
            get() = _status

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved : LiveData<Boolean>
        get() = _isSaved


    private val _electionName = MutableLiveData<String>()
    val electionsName : LiveData<String>
        get() = _electionName

    private val _electionDate = MutableLiveData<Date>()
    val electionsDate : LiveData<Date>
        get() = _electionDate


    private val _location = MutableLiveData<String>()
    val location : LiveData<String>
        get() = _location


    private val _ballot = MutableLiveData<String>()
    val ballot : LiveData<String>
        get() = _ballot


    private val _address = MutableLiveData<String>()
    val address : LiveData<String>
        get() = _address


    //TODO: Add var and methods to populate voter info
    private fun getVoterInfo(){
        viewModelScope.launch {
            try {
                var voterInfo = CivicsApi.retrofitService.getVoterInfoQuery(electionDivision.state, electionId)
                _electionName.value = voterInfo.election.name
                _electionDate.value = voterInfo.election.electionDay
                _location.value = if (voterInfo.state != null) {
                    voterInfo.state!!.first().electionAdministrationBody.votingLocationFinderUrl
                } else ""
                _ballot.value = if (voterInfo.state != null) {
                    voterInfo.state!!.first().electionAdministrationBody.ballotInfoUrl
                } else ""
                _address.value = getAddress(voterInfo)


            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
            }
        }
    }

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    fun saveElection(){
        viewModelScope.launch {
            val savedElectionEntity = SavedElectionEntity(electionId, _electionName.value!!, _electionDate.value!!, electionDivision)
            dataSource.insertSavedElection(savedElectionEntity)
            setButtonStatus()
        }
    }

    fun removeElection(){
        viewModelScope.launch {
            dataSource.deleteSavedElection(electionId)
            setButtonStatus()
        }
    }

    private fun getAddress(voterInfoResponse: VoterInfoResponse): String {
        var address: Address? = null
        if (voterInfoResponse.state != null){
            voterInfoResponse.state.first().electionAdministrationBody.correspondenceAddress?.let { add ->
                address = Address(add.line1, add.line2, add.city, add.state, add.zip)
            }
        }
        return address!!.toFormattedString()
    }

    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    private fun setButtonStatus() {
        viewModelScope.launch {
           var value = dataSource.getSavedElection(electionId) != null
        _isSaved.value = value
        }
    }

    fun resetStatus() {
        _status.value = ApiStatus.EMPTY
    }


        /**
         * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
         */

}

