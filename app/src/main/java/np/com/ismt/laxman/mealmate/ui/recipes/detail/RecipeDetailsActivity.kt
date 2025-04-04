package np.com.ismt.laxman.mealmate.ui.recipes.detail

import android.os.Bundle
import android.os.Parcel
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import np.com.ismt.laxman.mealmate.databinding.ActivityRecipeDetailsBinding
import np.com.ismt.laxman.mealmate.helpers.HelperUtil
import np.com.ismt.laxman.mealmate.models.Recipe
import np.com.ismt.laxman.mealmate.models.WeeklyMealPlan
import java.util.Calendar
import java.util.Date


class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailsBinding

    private var recipe: Recipe? = null
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        recipe = intent.getParcelableExtra("recipe")
        recipe?.apply {
            populateViews()
        }
        setUpBackButton()
    }

    private fun setUpBackButton() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun populateViews() {
        populateRecipeImage()
        populateRecipeName()
        populateRecipeCategory()
        populateCookingTime()
        populateDescription()
        populateIngredients()
        populateInstructions()
        populateNutritionalValue()
        setUpAddToMealPlanButton()
    }

    private fun populateRecipeImage() {
        val bitmap = HelperUtil.base64toBitmap(recipe!!.image, this.applicationContext)
        binding.ivRecipeImage.setImageBitmap(bitmap)
    }

    private fun populateRecipeName() {
        binding.tvRecipeName.text = recipe!!.name
    }

    private fun populateRecipeCategory() {
        binding.tvFoodCategory.text = recipe!!.category
    }

    private fun populateCookingTime() {
        binding.tvCookingTime.text = recipe!!.cookingTime
    }

    private fun populateDescription() {
        binding.tvDescription.text = recipe!!.description
    }

    private fun populateIngredients() {
        var data = ""
        (recipe!!.ingredients.isNotEmpty()).apply {
            recipe!!.ingredients.forEach { ingredient ->
                data += "- "
                    .plus(ingredient.quantity)
                    .plus(" ")
                    .plus(ingredient.unit)
                    .plus(" ")
                    .plus(ingredient.name)
                    .plus(", ")
                    .plus(ingredient.style)
                    .plus("\n")
            }
        }
        binding.tvIngredients.text = data
    }

    private fun populateInstructions() {
        var data = ""
        (recipe!!.instructions.isNotEmpty()).apply {
            recipe!!.instructions.forEach { instruction ->
                data += "- "
                    .plus(instruction)
                    .plus("\n\n")
            }
        }
        binding.tvInstructions.text = data
    }

    private fun populateNutritionalValue() {
        binding.tvCarbs.text = recipe!!.nutritionValue?.carbs
        binding.tvProtein.text = recipe!!.nutritionValue?.protein
        binding.tvCalories.text = recipe!!.nutritionValue?.calories
    }

    private fun setUpAddToMealPlanButton() {
        binding.mbAddRecipe.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDay = System.currentTimeMillis()

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        var endOfWeek = calendar.timeInMillis
        endOfWeek += (86400 * 1000)

        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(currentDay)
            .setEnd(endOfWeek)
            .setValidator(object : DateValidator {
                override fun describeContents(): Int {
                    return 0
                }

                override fun writeToParcel(dest: Parcel, flags: Int) {

                }

                override fun isValid(date: Long): Boolean {
                    return ((date >= currentDay) && (date <= endOfWeek))
                }
            })

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        builder.setCalendarConstraints(constraintsBuilder.build())
        builder.setTitleText("Select your day for this meal")

        val materialDatePicker = builder.build()
        materialDatePicker.show(supportFragmentManager, "DATE_PICKER")

        materialDatePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            val selectedMealDate = HelperUtil.formatDate(date)
            saveToWeeklyMealPlan(selectedMealDate)
        }
    }

    private fun saveToWeeklyMealPlan(mealDate: String) {
        val mealPlan = WeeklyMealPlan(date = mealDate, recipeId = recipe!!.id)

        val newDocument = db.collection("weeklyMealPlan").document()
        mealPlan.id = newDocument.id

        newDocument
            .set(mealPlan)
            .addOnSuccessListener {
                Log.d("RecipeDetailsActivity", "Meal : " + recipe!!.name + ", added")
                updateRecipeAsAddedToMealPlan()
            }
            .addOnFailureListener { exception ->
                Log.d("RecipeDetailsActivity", "Fail to add meal: " + recipe!!.name)
                exception.printStackTrace()
            }
    }

    private fun updateRecipeAsAddedToMealPlan() {
        val existingRecipe = db.collection("recipes").document(recipe!!.id)

        existingRecipe.update("isSelectedForMeal", true)
            .addOnSuccessListener {
                Log.d("RecipeDetailsActivity", "Recipe : " + recipe!!.name + ", updated")
                finish()
            }
            .addOnFailureListener { exception ->
                Log.d("RecipeDetailsActivity", "Fail to update recipe: " + recipe!!.name)
                exception.printStackTrace()
                finish()
            }
    }
}
