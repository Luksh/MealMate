package np.com.ismt.laxman.mealmate.ui.meals

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import np.com.ismt.laxman.mealmate.databinding.FragmentMealsBinding
import np.com.ismt.laxman.mealmate.helpers.VerticalSpacingDecorator
import np.com.ismt.laxman.mealmate.models.Recipe
import np.com.ismt.laxman.mealmate.models.WeeklyMealPlan

class MealsFragment : Fragment() {

    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!

    private var db: FirebaseFirestore? = null
    private lateinit var adapter: MealsAdapter
    private var recipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mealsViewModel =
            ViewModelProvider(this).get(MealsViewModel::class.java)

        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMealsAdapter()
        setUpRecyclerView()
        fetchRecipes()
    }
    override fun onStart() {
        super.onStart()
        binding.rvMeals.recycledViewPool.clear()
        if (this::adapter.isInitialized) {
            adapter.startListening()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        if (this::adapter.isInitialized) {
            adapter.stopListening()
        }
        super.onDestroy()
    }

    private fun setUpMealsAdapter() {
        val query = db!!.collection("weeklyMealPlan")

        val options = FirestoreRecyclerOptions.Builder<WeeklyMealPlan>()
            .setQuery(query, WeeklyMealPlan::class.java)
            .build()

        adapter = MealsAdapter(
            recipes,
            options
        )
    }

    private fun setUpRecyclerView() {
        _binding?.rvMeals?.layoutManager = LinearLayoutManager(requireActivity())
        _binding?.rvMeals?.apply {
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
        _binding?.rvMeals?.adapter = adapter
    }

    private fun fetchRecipes() {
        db?.apply {
            this.collection("recipes")
                .get()
                .addOnSuccessListener { results ->
                    validateRecipeFetchData(results)
                }
                .addOnFailureListener { error ->
                    Log.d("MealsFragment", "Unable to fetchRecipes: ".plus(error.message))
                }
        }
    }

    private fun validateRecipeFetchData(results: QuerySnapshot) {
        if (results.isEmpty) {
            binding.textNotifications.visibility = View.VISIBLE
            return
        } else {
            binding.textNotifications.visibility = View.GONE
        }

        for (document in results) {
            try {
                val recipeData = document.data
                val recipe = Recipe(
                    id = recipeData["id"] as? String ?: "",
                    image = recipeData["image"] as? String ?: "",
                    name = recipeData["name"] as? String ?: "",
                    description = recipeData["description"] as? String ?: "",
                    category = recipeData["category"] as? String ?: ""
                )
                recipes.add(recipe)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        if (recipes.isEmpty()) {
            binding.textNotifications.visibility = View.VISIBLE
            binding.rvMeals.visibility = View.GONE
        } else {
            binding.textNotifications.visibility = View.GONE
            binding.rvMeals.visibility = View.VISIBLE
        }
        adapter.notifyItemRangeChanged(0, recipes.size)
    }
}
