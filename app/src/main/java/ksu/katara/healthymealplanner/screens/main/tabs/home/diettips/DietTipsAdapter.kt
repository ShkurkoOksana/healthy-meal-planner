package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemDietTipBinding
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip

typealias DietTipActionListener = (dietTip: DietTip) -> Unit

class DietTipsAdapter(
    private val dietTipActionListener: DietTipActionListener
) : RecyclerView.Adapter<DietTipsAdapter.DietTipsViewHolder>(), View.OnClickListener {

    var dietTips: List<DietTipsListItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val dietTip = v.tag as DietTip
        dietTipActionListener.invoke(dietTip)
    }

    override fun getItemCount(): Int = dietTips.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietTipsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipBinding.inflate(inflater, parent, false)

        return DietTipsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipsViewHolder, position: Int) {
            val dietTipListItem = dietTips[position]
            val dietTip = dietTipListItem.dietTip

            with(holder.binding) {
                holder.itemView.tag = dietTip

                if (dietTipListItem.isInProgress) {
                    holder.binding.root.setOnClickListener(null)
                } else {
                    holder.binding.root.setOnClickListener(this@DietTipsAdapter)
                }

                dietTipNameTextView.text = dietTip.name

                if (dietTip.photo.isNotBlank()) {
                    Glide.with(dietTipPhotoImageView.context)
                        .load(dietTip.photo)
                        .circleCrop()
                        .placeholder(R.drawable.ic_diet_tip_details_default_background)
                        .error(R.drawable.ic_diet_tip_details_default_background)
                        .into(dietTipPhotoImageView)
                } else {
                    Glide.with(dietTipPhotoImageView.context).clear(dietTipPhotoImageView)
                    dietTipPhotoImageView.setImageResource(R.drawable.ic_diet_tip_details_default_background)
                }
            }
    }

    class DietTipsViewHolder(
        val binding: ItemDietTipBinding
    ) : RecyclerView.ViewHolder(binding.root)
}