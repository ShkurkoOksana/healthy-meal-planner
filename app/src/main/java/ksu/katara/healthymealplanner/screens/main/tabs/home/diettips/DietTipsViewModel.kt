package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip

class DietTipsViewModel(
    private val dietTipsRepository: DietTipsRepository
) : ViewModel(), DietTipActionListener {

    private val _dietTips = MutableLiveData<List<DietTip>>()
    val dietTips: LiveData<List<DietTip>> = _dietTips

    private val _actionShowDetails = MutableLiveData<DietTip>()
    val actionShowDetails: LiveData<DietTip> = _actionShowDetails

    init {
        loadDietTips()
    }

    private fun loadDietTips() {
        dietTipsRepository.loadDietTips()
        _dietTips.value = dietTipsRepository.getDietTips()
    }

    override fun invoke(dietTip: DietTip) {
        _actionShowDetails.value = dietTip
    }
}
