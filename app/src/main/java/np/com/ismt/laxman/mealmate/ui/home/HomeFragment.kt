package np.com.ismt.laxman.mealmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import np.com.ismt.laxman.mealmate.databinding.FragmentHomeBinding
import np.com.ismt.laxman.mealmate.models.Recipe

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.btnAddDefaultRecipes?.setOnClickListener {
            addDefaultRecipes()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addDefaultRecipes() {
        val string = requireContext().assets.open("default_recipes.json").bufferedReader().use {
            it.readText()
        }
        val defaultRecipes = Gson().fromJson(string, Array<Recipe>::class.java).toList()
        db?.apply {
            defaultRecipes.forEach { recipe ->
                val newDocument = this.collection("recipes").document()
                recipe.id = newDocument.id
                newDocument
                    .set(recipe)
                    .addOnSuccessListener {
                        Log.d("HomeFragment", "Recipe : " + recipe.name + ", added")
                    }
                    .addOnFailureListener {
                        Log.d("HomeFragment", "Fail to add recipe: " + recipe.name)
                    }
            }
        }
    }
}
