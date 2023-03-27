package ksu.katara.healthymealplanner.screens.main.tabs.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.FragmentHomeBinding
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.screens.main.tabs.home.diettips.DietTipsAdapter
import ksu.katara.healthymealplanner.screens.main.tabs.home.diettips.DietTipsViewModel
import ksu.katara.healthymealplanner.utils.viewModelCreator

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dietTipsAdapter: DietTipsAdapter

    private val dietTipsViewModel by viewModelCreator { DietTipsViewModel(Repositories.dietTipsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        initDietTipsSection()
    }

    private fun initDietTipsSection() {
        dietTipsAdapter = DietTipsAdapter(dietTipsViewModel)

        dietTipsViewModel.dietTips.observe(viewLifecycleOwner) {
            dietTipsAdapter.dietTips = it
        }

        dietTipsViewModel.actionShowDetails.observe(viewLifecycleOwner, Observer {
            onDietTipPressed(it)
        })

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dietTipsRecycleView.layoutManager = layoutManager
        binding.dietTipsRecycleView.adapter = dietTipsAdapter
    }

    private fun onDietTipPressed(dietTip: DietTip) {
        //TODO(realize fragment with dietTip details)
    }
}
