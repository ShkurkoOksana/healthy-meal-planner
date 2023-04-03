package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.FragmentDietTipDetailsBinding
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.viewModelCreator
import kotlin.properties.Delegates

class DietTipDetailsFragment : Fragment() {

    private lateinit var binding: FragmentDietTipDetailsBinding

    private val viewModel by viewModelCreator { DietTipDetailsViewModel(Repositories.dietTipsRepository) }

    private val args by navArgs<DietTipDetailsFragmentArgs>()

    private var dietTipId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && getDietTipArgument() != null) {
            dietTipId = getDietTipArgument()
        }

        viewModel.loadDietTip(dietTipId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDietTipDetailsBinding.inflate(layoutInflater, container, false)

        viewModel.state.observe(viewLifecycleOwner) {
            binding.dietTipDetailsContentContainer.visibility = if (it.showContent) {
                val dietTipDetails = (it.dietTipDetailsResult as SuccessResult).data
                binding.dietTipDetailsTitleTextView.text = dietTipDetails.title
                binding.dietTipDetailsDescriptionTextView.text = dietTipDetails.description
                if (dietTipDetails.background.isNotBlank()) {
                    Glide.with(this)
                        .load(dietTipDetails.background)
                        .into(binding.dietTipDetailsBackgroundImageView)
                } else {
                    Glide.with(this)
                        .load(R.drawable.ic_diet_tip_details_default_background)
                        .into(binding.dietTipDetailsBackgroundImageView)
                }

                View.VISIBLE
            } else {
                View.GONE
            }

            binding.dietTipDetailsProgressBar.visibility = if (it.showProgress) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    private fun getDietTipArgument(): Long = args.dietTipId
}