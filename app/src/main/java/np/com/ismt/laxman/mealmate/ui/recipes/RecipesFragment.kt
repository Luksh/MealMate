package np.com.ismt.laxman.mealmate.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import np.com.ismt.laxman.mealmate.databinding.FragmentRecipesBinding
import np.com.ismt.laxman.mealmate.helpers.VerticalSpacingDecorator
import np.com.ismt.laxman.mealmate.models.Recipe
import np.com.ismt.laxman.mealmate.ui.recipes.add.AddRecipeActivity
import np.com.ismt.laxman.mealmate.ui.recipes.detail.RecipeDetailsActivity

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db: FirebaseFirestore? = null
    private lateinit var adapter: RecipesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recipesViewModel =
            ViewModelProvider(this)[RecipesViewModel::class.java]

        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecipes()
        setUpAddRecipe()
    }

    override fun onStart() {
        super.onStart()
        binding.rvRecipes.recycledViewPool.clear()
        adapter.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        adapter.stopListening()
        super.onDestroy()
    }

    private fun setUpRecipes() {
        db?.apply {
            setUpRecipesAdapter()
            _binding?.rvRecipes?.layoutManager = LinearLayoutManager(requireActivity())
            _binding?.rvRecipes?.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.addItemDecoration(
                    VerticalSpacingDecorator(
                        top = 0,
                        start = 0,
                        end = 0,
                        bottom = 24
                    )
                )
            }
            _binding?.rvRecipes?.adapter = adapter
        }
    }

    private fun setUpRecipesAdapter() {
        val query = db!!.collection("recipes")

        val options = FirestoreRecyclerOptions.Builder<Recipe>()
            .setQuery(query, Recipe::class.java)
            .build()

        adapter = RecipesAdapter(
            options,
            onRecipeClicked = { recipe ->
                openRecipeDetails(recipe)
            }
        )
    }

    private fun setUpAddRecipe() {
        _binding?.fabAddRecipe?.setOnClickListener {
            startActivity(Intent(requireActivity(), AddRecipeActivity::class.java))
        }
    }

    private fun openRecipeDetails(recipe: Recipe) {
        val intent = Intent(requireActivity(), RecipeDetailsActivity::class.java)
        intent.apply {
            this.putExtra("recipe", recipe)
            startActivity(this)
        }
    }
}
