package ksu.katara.healthymealplanner.model.dietTips

import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip

class InMemoryDietTipsRepository : DietTipsRepository {

    private var dietTips = mutableListOf<DietTip>()

    override fun loadDietTips() {
        dietTips = (1 until DIET_TIP_IMAGES.size).map {
            DietTip(
                id = it.toLong(),
                name = DIET_TIP_NAMES[it],
                photo = DIET_TIP_IMAGES[it]
            )
        }.toMutableList()
    }

    override fun getDietTips() = dietTips

    companion object {
        private val DIET_TIP_IMAGES = mutableListOf(
            "https://images.unsplash.com/photo-1490645935967-10de6ba17061?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2353&q=80",
            "https://images.unsplash.com/photo-1614887065001-06c958a7cddd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            "https://images.unsplash.com/photo-1656218257936-8384471a0258?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2274&q=80",
            "https://images.unsplash.com/photo-1518611012118-696072aa579a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
            "https://images.unsplash.com/photo-1545389336-cf090694435e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1364&q=80",
            "https://images.unsplash.com/photo-1641679103706-fc8542e2a97a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
            "https://images.unsplash.com/photo-1509440159596-0249088772ff?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2372&q=80",
            "https://plus.unsplash.com/premium_photo-1664647903833-318dce8f3239?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            "https://images.unsplash.com/photo-1610415958681-7aabb05711f5?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            "https://images.unsplash.com/photo-1556386734-4227a180d19e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1305&q=80",
            "https://images.unsplash.com/photo-1572357176061-7c96fd2af22f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1335&q=80",
            "https://images.unsplash.com/photo-1490645935967-10de6ba17061?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2353&q=80"
        )

        private val DIET_TIP_NAMES = mutableListOf(
            "Diet",
            "Water",
            "Dream",
            "Sport",
            "Meditation",
            "Inflammation products: sugar",
            "Inflammation products: gluten",
            "Inflammation products: dairy",
            "FODMAP diet",
            "AIP diet",
            "Candida diet",
            "LCHF diet"
        )
    }
}
