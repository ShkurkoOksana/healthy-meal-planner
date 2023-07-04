package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentRecipeCategoriesBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.screenViewModel

class RecipeCategoriesFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments.
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentRecipeCategoriesBinding

    private lateinit var recipeCategoriesAdapter: RecipeCategoriesAdapter

    override val viewModel by screenViewModel<RecipeCategoriesViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCategoriesBinding.inflate(layoutInflater, container, false)
        arguments = bundleOf(BaseScreen.ARG_SCREEN to Screen())
        initRecipeCategories()
        return binding.root
    }

    private fun initRecipeCategories() {
        recipeCategoriesAdapter = RecipeCategoriesAdapter(viewModel)
        viewModel.recipeCategories.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    binding.recipeCategoriesRecyclerView.visibility = View.VISIBLE
                    recipeCategoriesAdapter.recipeCategories = it.data
                }
                is ErrorResult -> {
                    binding.noRecipeCategoriesTextView.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    binding.recipesRecipeCategoriesProgressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.noRecipeCategoriesTextView.visibility = View.VISIBLE
                }
            }
        }
        val recipeCategoriesLayoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recipeCategoriesRecyclerView.layoutManager = recipeCategoriesLayoutManager
        binding.recipeCategoriesRecyclerView.adapter = recipeCategoriesAdapter
        val recipeCategoriesAnimator = binding.recipeCategoriesRecyclerView.itemAnimator
        if (recipeCategoriesAnimator is DefaultItemAnimator) {
            recipeCategoriesAnimator.supportsChangeAnimations = false
        }
    }

    private fun hideAll() = with(binding) {
        recipeCategoriesRecyclerView.visibility = View.GONE
        recipesRecipeCategoriesProgressBar.visibility = View.GONE
        noRecipeCategoriesTextView.visibility = View.GONE
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}