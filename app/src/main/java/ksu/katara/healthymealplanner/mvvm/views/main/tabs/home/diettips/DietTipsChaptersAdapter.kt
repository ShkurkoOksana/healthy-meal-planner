package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.databinding.ItemDietTipsChapterBinding
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter

class DietTipsChaptersDiffCallback(
    private val oldList: List<DietTipChapter>,
    private val newList: List<DietTipChapter>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldDietTipsChapter = oldList[oldItemPosition]
        val newDietTipsChapter = newList[newItemPosition]
        return oldDietTipsChapter.id == newDietTipsChapter.id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldDietTipsChapter = oldList[oldItemPosition]
        val newDietTipsChapter = newList[newItemPosition]
        return oldDietTipsChapter == newDietTipsChapter
    }
}

class DietTipsChaptersAdapter(
    private val context: Context,
    private val dietTipsChaptersViewModel: DietTipsChaptersViewModel,
) : RecyclerView.Adapter<DietTipsChaptersAdapter.DietTipsChaptersViewHolder>() {

    var dietTipsChapters: List<DietTipChapter> = emptyList()
        set(newValue) {
            val diffCallback = DietTipsChaptersDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount(): Int = dietTipsChapters.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietTipsChaptersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipsChapterBinding.inflate(inflater, parent, false)
        return DietTipsChaptersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipsChaptersViewHolder, position: Int) {
        val dietTipsChapter = dietTipsChapters[position]

        with(holder.binding) {
            holder.itemView.tag = dietTipsChapter
            dietTipChapterTitleTextView.text = dietTipsChapter.name
            val dietTipsAdapter = DietTipsAdapter(dietTipsChaptersViewModel)
            dietTipsAdapter.dietTips = dietTipsChapter.dietTipsList
            dietTipsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            dietTipsRecyclerView.adapter = dietTipsAdapter
        }
    }

    class DietTipsChaptersViewHolder(
        val binding: ItemDietTipsChapterBinding
    ) : RecyclerView.ViewHolder(binding.root)
}