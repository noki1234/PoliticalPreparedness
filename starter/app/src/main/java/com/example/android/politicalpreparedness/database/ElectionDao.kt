package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.ElectionEntity
import com.example.android.politicalpreparedness.network.models.SavedElectionEntity

@Dao
interface ElectionDao {

    //UpcomingElectionDao---------------------------------------------------------------
    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingElection(election: ElectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUpcomingElections(vararg election: ElectionEntity)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table ORDER BY id DESC")
    fun getAllUpcomingElections(): LiveData<List<ElectionEntity>>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id=:id")
    suspend fun getUpcomingElection(id: Int): ElectionEntity

    //SavedElectionDao---------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedElection(savedElection: SavedElectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSavedElections(vararg savedElection: SavedElectionEntity)

    @Query("SELECT * FROM saved_election_table ORDER BY id DESC")
    fun getAllSavedElections(): LiveData<List<SavedElectionEntity>>

    @Query("SELECT * FROM saved_election_table WHERE id=:id")
    suspend fun getSavedElection(id: Int): SavedElectionEntity?

    //TODO: Add delete query
    @Query("DELETE FROM saved_election_table WHERE id=:id")
    suspend fun deleteSavedElection(id: Int)

    //TODO: Add clear query
    @Query("DELETE FROM saved_election_table")
    suspend fun deleteAllSavedElections()
}