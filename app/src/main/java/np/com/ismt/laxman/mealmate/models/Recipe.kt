package np.com.ismt.laxman.mealmate.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    var id: String = "",
    val name: String = "",
    val image: String = "",
    val description: String = "",
    val category: String = "",
    val cookingTime: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val nutritionValue: NutritionValue? = null,
    @field:JvmField
    var isMyRecipe: Boolean = false,
    @field:JvmField
    var isSelectedForMeal: Boolean = false
): Parcelable

@Parcelize
data class Ingredient(
    val name: String = "",
    val style: String = "",
    val quantity: String = "",
    val unit: String = ""
): Parcelable

@Parcelize
data class NutritionValue(
    val carbs: String = "",
    val protein: String = "",
    val calories: String = ""
): Parcelable
