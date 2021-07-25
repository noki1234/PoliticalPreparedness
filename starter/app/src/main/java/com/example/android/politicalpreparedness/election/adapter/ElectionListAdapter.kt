package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionItemBinding
import com.example.android.politicalpreparedness.election.models.Election

class ElectionListAdapter(private val onClickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(item)
        }
        holder.bind(item)
    }
}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(val binding: ElectionItemBinding): RecyclerView.ViewHolder(binding.root) {

   //TODO: Add companion object to inflate ViewHolder (from)
    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ElectionItemBinding.inflate(layoutInflater, parent, false)

            return ElectionViewHolder(binding)
        }
    }

    fun bind(item: Election) {
        binding.election = item
        binding.executePendingBindings()
    }
}
//TODO: Create ElectionDiffCallback
class ElectionDiffCallback: DiffUtil.ItemCallback<Election>(){
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

}
//TODO: Create ElectionListener
class ElectionListener(val clickListener: (election: Election) -> Unit){
    fun onClick(election: Election) = clickListener(election)
}