package ksu.katara.healthymealplanner.mvvm.views.main.tabs.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ksu.katara.healthymealplanner.databinding.FragmentShoppingListBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.collectFlow
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult

@AndroidEntryPoint
class ShoppingListFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments.
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentShoppingListBinding

    private lateinit var shoppingListAdapter: ShoppingListAdapter

    override val viewModel by viewModels<ShoppingListViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoppingListBinding.inflate(layoutInflater, container, false)
        arguments = bundleOf(BaseScreen.ARG_SCREEN to Screen())
        initView()
        return binding.root
    }

    private fun initView() {
        shoppingListAdapter = ShoppingListAdapter(requireContext(), viewModel)
        collectFlow(viewModel.shoppingList) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    shoppingListAdapter.shoppingList = it
                }
            )
            onTryAgain(binding.root) {
                viewModel.loadAgain()
            }
            val shoppingListLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.shoppingListRecyclerView.layoutManager = shoppingListLayoutManager
            binding.shoppingListRecyclerView.adapter = shoppingListAdapter
            val shoppingListAnimator = binding.shoppingListRecyclerView.itemAnimator
            if (shoppingListAnimator is DefaultItemAnimator) {
                shoppingListAnimator.supportsChangeAnimations = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadAgain()
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}