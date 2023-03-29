package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.FragmentDietTipDetailsBinding
import kotlinx.android.synthetic.main.fragment_diet_tip_details.*
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.utils.viewModelCreator

class DietTipDetailsFragment : Fragment(R.layout.fragment_diet_tip_details) {

    private lateinit var binding: FragmentDietTipDetailsBinding

    private val args by navArgs<DietTipDetailsFragmentArgs>()

    private val dietTipDetailsViewModel by viewModelCreator {
        DietTipDetailsViewModel(
            getDietTipId(),
            Repositories.dietTipsRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDietTipDetailsBinding.bind(view)

        initView()
    }

    private fun initView() {
        dietTipDetailsViewModel.dietTipsDetails.observe(viewLifecycleOwner) {
            with(binding) {
                Glide.with(dietTipDetailsBackgroundImageView.context)
                    .load(it.background)
                    .placeholder(R.drawable.ic_diet_tip_details_default_background)
                    .error(R.drawable.ic_diet_tip_details_default_background)
                    .into(dietTipDetailsBackgroundImageView)
            }

            dietTipDetailsTitleTextView.text = it.title
            dietTipDetailsDescriptionTextView.text = it.description
        }
    }

    private fun getDietTipId() = args.dietTipId
}