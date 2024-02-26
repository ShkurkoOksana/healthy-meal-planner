package ksu.katara.healthymealplanner.mvvm.data.dietTips.entities

import ksu.katara.healthymealplanner.mvvm.domain.dietTips.entities.DietTipDetailSteps
import org.junit.Assert.assertEquals
import org.junit.Test

class DietTipDetailStepDBEntityTest {

    @Test
    fun toDietTipDetailSteps() {
        val responseEntity = DietTipDetailStepDBEntity(
            id = 1,
            indexNumber = 1,
            titleName = "Подготовка к приему пищи",
            titleDescription = "- Пищеварение начинается с мысли о еде\\n- Едим по голоду, не заедаем стресс\\n- Внешний вид еды играет важную роль\\n- Не есть на ходу, стоя\\n- Есть за столом в спокойном состоянии, не испытывая стресс\\n- Не использовать гаджены во время приема пищи\\n- Для лучшего контроля тщытельного пережевывания: откусите кусочек, положите вилку на стол. Тщательно пережуйте. Возьмите вилку и положите следующую порцию еды в рот",
            dietTipDetailsId = 1
        )

        val inAppEntity = responseEntity.toDietTipDetailSteps()

        val expectedInAppEntity = DietTipDetailSteps(
            id = 1,
            indexNumber = 1,
            title = "Подготовка к приему пищи",
            description = "- Пищеварение начинается с мысли о еде\\n- Едим по голоду, не заедаем стресс\\n- Внешний вид еды играет важную роль\\n- Не есть на ходу, стоя\\n- Есть за столом в спокойном состоянии, не испытывая стресс\\n- Не использовать гаджены во время приема пищи\\n- Для лучшего контроля тщытельного пережевывания: откусите кусочек, положите вилку на стол. Тщательно пережуйте. Возьмите вилку и положите следующую порцию еды в рот",
            dietTipDetailId = 1
        )
        assertEquals(inAppEntity, expectedInAppEntity)
    }
}