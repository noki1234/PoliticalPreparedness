package com.example.android.politicalpreparedness.network.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.politicalpreparedness.election.models.Election
import com.squareup.moshi.Json
import java.util.*

@Entity(tableName = "saved_election_table")
data class SavedElectionEntity(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "electionDay") val electionDay: Date,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
)


fun List<SavedElectionEntity>.asDomainModel() : List<Election> {
    return map {
        Election(
                id = it.id,
                name = it.name,
                electionDay = it.electionDay,
                division = it.division
        )
    }
}