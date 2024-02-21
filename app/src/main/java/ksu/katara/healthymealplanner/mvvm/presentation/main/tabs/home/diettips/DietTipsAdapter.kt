package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.diettips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemDietTipBinding
import ksu.katara.healthymealplanner.mvvm.domain.dietTips.entities.DietTip

interface DietTipActionListener {

    fun onDietTipPressed(dietTipId: Long)

}

class DietTipsDiffCallback(
    private val oldList: List<DietTip>,
    private val newList: List<DietTip>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldDietTip = oldList[oldItemPosition]
        val newDietTip = newList[newItemPosition]
        return oldDietTip.id == newDietTip.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldDietTip = oldList[oldItemPosition]
        val newDietTip = newList[newItemPosition]
        return oldDietTip == newDietTip
    }
}

class DietTipsAdapter(
    private val dietTipActionListener: DietTipActionListener
) : RecyclerView.Adapter<DietTipsAdapter.DietTipsViewHolder>(), View.OnClickListener {

    var dietTips: List<DietTip> = emptyList()
        set(newValue) {
            val diffCallback = DietTipsDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onClick(v: View) {
        val dietTip = v.tag as DietTip
        dietTipActionListener.onDietTipPressed(dietTip.id)
    }

    override fun getItemCount(): Int = dietTips.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietTipsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipBinding.inflate(inflater, parent, false)

        return DietTipsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipsViewHolder, position: Int) {
        val dietTip = dietTips[position]

        with(holder.binding) {
            holder.itemView.tag = dietTip
            holder.binding.root.setOnClickListener(this@DietTipsAdapter)
            dietTipNameTextView.text = dietTip.name
            if (dietTip.photo.isNotBlank()) {
                Glide.with(dietTipPhotoImageView.context)
                    .load(dietTip.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_diet_tip_details)
                    .error(R.drawable.ic_diet_tip_details)
                    .into(dietTipPhotoImageView)
            } else {
                Glide.with(dietTipPhotoImageView.context).clear(dietTipPhotoImageView)
                dietTipPhotoImageView.setImageResource(R.drawable.ic_diet_tip_details)
            }
        }
    }

    class DietTipsViewHolder(
        val binding: ItemDietTipBinding
    ) : RecyclerView.ViewHolder(binding.root)
}