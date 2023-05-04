package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.databinding.ItemDietTipsChapterBinding

class DietTipsChaptersAdapter(
    private val context: Context,
    private val dietTipsViewModel: DietTipsViewModel,
) : RecyclerView.Adapter<DietTipsChaptersAdapter.DietTipsChaptersViewHolder>() {

    var dietTipsChapters: List<DietTipsChaptersListItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = dietTipsChapters.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietTipsChaptersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDietTipsChapterBinding.inflate(inflater, parent, false)

        return DietTipsChaptersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipsChaptersViewHolder, position: Int) {
        val dietTipsChaptersListItem = dietTipsChapters[position]
        val dietTipsChapter = dietTipsChaptersListItem.dietTipsChapter

        with(holder.binding) {
            holder.itemView.tag = dietTipsChapter

            dietTipsChaptersTitleTextView.text = dietTipsChapter.name

            val dietTipsAdapter = DietTipsAdapter(dietTipsViewModel)

            dietTipsAdapter.dietTips = dietTipsChapter.dietTipsList.map {
                DietTipsListItem(it, false)
            }

            dietTipsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            dietTipsRecyclerView.adapter = dietTipsAdapter
        }
    }

    class DietTipsChaptersViewHolder(
        val binding: ItemDietTipsChapterBinding
    ) : RecyclerView.ViewHolder(binding.root)
}