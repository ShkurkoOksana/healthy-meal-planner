package ksu.katara.healthymealplanner.mvvm.views.main.tabs.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentShoppingListBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
import ksu.katara.healthymealplanner.foundation.views.screenViewModel

class ShoppingListFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments.
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentShoppingListBinding

    private lateinit var shoppingListAdapter: ShoppingListAdapter

    override val viewModel by screenViewModel<ShoppingListViewModel>()

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
        viewModel.shoppingList.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    shoppingListAdapter.shoppingList = it
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
        val shoppingListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.shoppingListRecyclerView.layoutManager = shoppingListLayoutManager
        binding.shoppingListRecyclerView.adapter = shoppingListAdapter
        val shoppingListAnimator = binding.shoppingListRecyclerView.itemAnimator
        if (shoppingListAnimator is DefaultItemAnimator) {
            shoppingListAnimator.supportsChangeAnimations = false
        }
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}