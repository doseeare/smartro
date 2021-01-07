package ru.smartro.worknote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.choose_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.body.TypeAppBody

class TypeAppAdapter(private val items: ArrayList<TypeAppBody>) :
    RecyclerView.Adapter<TypeAppAdapter.OwnerViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.choose_item, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val organisation = items[position]

            if (checkedPosition == -1){
                holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            }else{
                if (checkedPosition == holder.adapterPosition){
                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                }
                else{
                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                }
            }

            holder.itemView.choose_title.text = organisation.name
            holder.itemView.setOnClickListener {
                holder.itemView.choose_cardview.isVisible = true
                if (checkedPosition != holder.adapterPosition){
                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                    notifyItemChanged(checkedPosition)
                    checkedPosition = holder.adapterPosition
                }
            }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}