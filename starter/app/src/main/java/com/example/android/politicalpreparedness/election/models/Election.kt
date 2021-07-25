package com.example.android.politicalpreparedness.election.models

import android.os.Parcelable
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.ElectionEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Election (val id: Int,
                     val name: String,
                     val electionDay: Date,
                     val division: Division): Parcelable


    fun List<Election>.asDatabaseModel(): Array<ElectionEntity> {
        return this.map {
            ElectionEntity (
                    id = it.id,
                    name = it.name,
                    electionDay = it.electionDay,
                    division = it.division)
        }.toTypedArray()
}
