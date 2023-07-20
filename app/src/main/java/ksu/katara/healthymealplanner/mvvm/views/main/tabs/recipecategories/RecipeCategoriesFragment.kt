package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentRecipeCategoriesBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
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
        initView()
        return binding.root
    }

    private fun initView() {
        recipeCategoriesAdapter = RecipeCategoriesAdapter(viewModel)
        viewModel.recipeCategories.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    recipeCategoriesAdapter.recipeCategories = it
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.tryAgain()
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

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}