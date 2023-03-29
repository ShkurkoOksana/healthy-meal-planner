package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.ItemDietTipBinding
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip

typealias DietTipActionListener = (dietTip: DietTip) -> Unit

class DietTipsAdapter(
    private val actionListener: DietTipActionListener
) : RecyclerView.Adapter<DietTipsAdapter.DietTipsViewHolder>(), View.OnClickListener {

    var dietTips: List<DietTip> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val dietTip = v.tag as DietTip
        actionListener.invoke(dietTip)
    }

    override fun getItemCount() = AMOUNT_VISIBLE_DIET_TIPS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietTipsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipBinding.inflate(inflater, parent, false)

        return DietTipsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipsViewHolder, position: Int) {
        val dietTip = dietTips[position]
        holder.itemView.tag = dietTip
        holder.binding.root.setOnClickListener(this@DietTipsAdapter)

        with(holder.binding) {
            if (position < AMOUNT_VISIBLE_DIET_TIPS - 1) {
                dietTipNameTextView.text = dietTip.name

                Glide.with(dietTipPhotoImageView.context)
                    .load(dietTip.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_diet_tip_default_photo)
                    .error(R.drawable.ic_diet_tip_default_photo)
                    .into(dietTipPhotoImageView)
            } else {
                dietTipNameTextView.text = holder.itemView.context.getString(R.string.more_diet_tips)
                Glide.with(dietTipPhotoImageView.context)
                    .load(R.drawable.ic_diet_tips_more)
                    .circleCrop()
                    .placeholder(R.drawable.ic_diet_tips_more)
                    .error(R.drawable.ic_diet_tips_more)
                    .into(dietTipPhotoImageView)
            }
        }
    }

    class DietTipsViewHolder(
        val binding: ItemDietTipBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val AMOUNT_VISIBLE_DIET_TIPS = 5
    }
}
