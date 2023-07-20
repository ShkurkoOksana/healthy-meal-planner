package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipesincategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentRecipesInCategoryBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
import ksu.katara.healthymealplanner.foundation.views.screenViewModel
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.Category

class RecipesInCategoryFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: selected recipeCategory.
     */
    class Screen(
        val recipeCategory: Category
    ) : BaseScreen

    private lateinit var binding: FragmentRecipesInCategoryBinding

    private lateinit var recipesInCategoryAdapter: RecipesInCategoryAdapter

    override val viewModel by screenViewModel<RecipesInCategoryViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesInCategoryBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        recipesInCategoryAdapter = RecipesInCategoryAdapter(viewModel)
        viewModel.recipesInCategory.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    recipesInCategoryAdapter.recipesInCategory = it
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
        val recipeCategoriesLayoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recipesInCategoryRecyclerView.layoutManager =
            recipeCategoriesLayoutManager
        binding.recipesInCategoryRecyclerView.adapter = recipesInCategoryAdapter
        val recipesInCategoryAnimator = binding.recipesInCategoryRecyclerView.itemAnimator
        if (recipesInCategoryAnimator is DefaultItemAnimator) {
            recipesInCategoryAnimator.supportsChangeAnimations = false
        }
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}