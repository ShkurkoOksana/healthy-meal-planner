package ksu.katara.healthymealplanner.screens.main.tabs.recipes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.FragmentRecipesBinding

class RecipesFragment : Fragment(R.layout.fragment_recipes) {

    private lateinit var binding: FragmentRecipesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipesBinding.bind(view)
    }
}
