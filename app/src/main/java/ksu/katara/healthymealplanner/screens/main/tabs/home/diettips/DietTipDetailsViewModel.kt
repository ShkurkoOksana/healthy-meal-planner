package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails

class DietTipDetailsViewModel(
    private val dietTipId: Long,
    private val dietTipsRepository: DietTipsRepository
) : ViewModel() {

    private val _dietTipDetails = MutableLiveData<DietTipDetails>()
    val dietTipsDetails: LiveData<DietTipDetails> = _dietTipDetails

    init {
        loadDietTipDetails(dietTipId)
    }

    private fun loadDietTipDetails(id: Long) {
        _dietTipDetails.value = dietTipsRepository.getById(id)
    }
}