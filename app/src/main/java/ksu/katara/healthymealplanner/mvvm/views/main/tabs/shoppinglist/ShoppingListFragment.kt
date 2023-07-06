package ksu.katara.healthymealplanner.mvvm.views.main.tabs.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentShoppingListBinding
import ksu.katara.healthymealplanner.databinding.PartResultBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.screenViewModel

class ShoppingListFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments.
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var resultBinding: PartResultBinding

    private lateinit var shoppingListAdapter: ShoppingListAdapter

    override val viewModel by screenViewModel<ShoppingListViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoppingListBinding.inflate(layoutInflater, container, false)
        resultBinding = PartResultBinding.bind(binding.root)
        arguments = bundleOf(BaseScreen.ARG_SCREEN to Screen())
        initShoppingList()
        return binding.root
    }

    private fun initShoppingList() {
        shoppingListAdapter = ShoppingListAdapter(requireContext(), viewModel)
        viewModel.shoppingList.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    binding.shoppingListRecyclerView.visibility = View.VISIBLE
                    shoppingListAdapter.shoppingList = it.data
                }
                is ErrorResult -> {
                    resultBinding.errorContainer.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    resultBinding.progressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    resultBinding.noData.visibility = View.VISIBLE
                }
            }
        }
        val shoppingListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.shoppingListRecyclerView.layoutManager = shoppingListLayoutManager
        binding.shoppingListRecyclerView.adapter = shoppingListAdapter
        val shoppingListAnimator = binding.shoppingListRecyclerView.itemAnimator
        if (shoppingListAnimator is DefaultItemAnimator) {
            shoppingListAnimator.supportsChangeAnimations = false
        }
    }

    private fun hideAll() {
        binding.shoppingListRecyclerView.visibility = View.GONE
        resultBinding.progressBar.visibility = View.GONE
        resultBinding.errorContainer.visibility = View.GONE
        resultBinding.noData.visibility = View.GONE
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}