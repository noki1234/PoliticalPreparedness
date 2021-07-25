package com.example.android.politicalpreparedness.network.models

import androidx.room.*
import com.example.android.politicalpreparedness.election.models.Election
import com.squareup.moshi.*
import java.util.*

@Entity(tableName = "election_table")
data class ElectionEntity(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "electionDay") val electionDay: Date,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
)


fun List<ElectionEntity>.asDomainModel() : List<Election> {
    return map {
        Election(
                id = it.id,
                name = it.name,
                electionDay = it.electionDay,
                division = it.division
                )
    }
}