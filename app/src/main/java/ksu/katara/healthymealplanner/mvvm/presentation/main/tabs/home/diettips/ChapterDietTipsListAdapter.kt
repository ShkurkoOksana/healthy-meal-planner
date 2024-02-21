package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.diettips

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.databinding.ItemChapterDietTipsBinding

class ChapterDietTipsListDiffCallback(
    private val oldList: List<ChapterDietTips>,
    private val newList: List<ChapterDietTips>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldChapterDietTips = oldList[oldItemPosition]
        val newChapterDietTips = newList[newItemPosition]
        return oldChapterDietTips.chapter == newChapterDietTips.chapter &&
                oldChapterDietTips.dietTips == newChapterDietTips.dietTips
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldChapterDietTips = oldList[oldItemPosition]
        val newChapterDietTips = newList[newItemPosition]
        return oldChapterDietTips == newChapterDietTips
    }
}

class ChapterDietTipsListAdapter(
    private val context: Context,
    private val dietTipsChaptersViewModel: DietTipsChaptersViewModel,
) : RecyclerView.Adapter<ChapterDietTipsListAdapter.DietTipsChaptersViewHolder>() {

    var chapterDietTipsList: List<ChapterDietTips> = emptyList()
        set(newValue) {
            val diffCallback = ChapterDietTipsListDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount(): Int = chapterDietTipsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietTipsChaptersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChapterDietTipsBinding.inflate(inflater, parent, false)
        return DietTipsChaptersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietTipsChaptersViewHolder, position: Int) {
        val chapterDietTips = chapterDietTipsList[position]
        val chapter = chapterDietTips.chapter
        val dietTips = chapterDietTips.dietTips

        with(holder.binding) {
            this.dietTipChapterTitleTextView.text = chapter.name

            val dietTipsAdapter = DietTipsAdapter(dietTipsChaptersViewModel)
            val dietTipsLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            dietTipsRecyclerView.layoutManager = dietTipsLayoutManager
            dietTipsRecyclerView.adapter = dietTipsAdapter
            dietTipsAdapter.dietTips = dietTips
        }

    }

    class DietTipsChaptersViewHolder(
        val binding: ItemChapterDietTipsBinding
    ) : RecyclerView.ViewHolder(binding.root)

}