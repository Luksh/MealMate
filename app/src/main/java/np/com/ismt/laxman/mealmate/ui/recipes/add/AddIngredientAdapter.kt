package np.com.ismt.laxman.mealmate.ui.recipes.add

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import np.com.ismt.laxman.mealmate.databinding.LayoutAddIngredientItemBinding
import np.com.ismt.laxman.mealmate.models.Ingredient

class AddIngredientAdapter(
    private val ingredients: List<Ingredient>,
    private val onRemoveIngredient: (Int) ->Unit
): RecyclerView.Adapter<AddIngredientAdapter.AddIngredientViewHolder>() {

    inner class AddIngredientViewHolder(
        private val binding: LayoutAddIngredientItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        private var ingredientName = ""
        private var ingredientQty = ""
        private var ingredientUnit = ""

        init {
            binding.tietAddIngredientName.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    ingredientName = s.toString()
                    assignIngredientData()
                }
            })

            binding.tietAddIngredientQty.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    ingredientQty = s.toString()
                    assignIngredientData()
                }
            })

            binding.tietAddIngredientUnit.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    ingredientUnit = s.toString()
                    assignIngredientData()
                }

            })
        }

        fun bindView(ingredient: Ingredient) {
            ingredientName = ingredient.name?: ""
            ingredientQty = ingredient.quantity?: ""
            ingredientUnit = ingredient.unit?: ""

            binding.apply {
                tietAddIngredientName.setText(ingredient.name)
                tietAddIngredientQty.setText(ingredient.quantity)
                tietAddIngredientUnit.setText(ingredient.unit)

                ibRemoveIngredient.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onRemoveIngredient(position)
                    }
                }
            }
        }

        private fun assignIngredientData() {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val updatedIngredient = Ingredient(
                    name = ingredientName,
                    quantity = ingredientQty,
                    unit = ingredientUnit
                )
                (ingredients as MutableList<Ingredient>)[position] = updatedIngredient
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddIngredientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutAddIngredientItemBinding.inflate(layoutInflater, parent, false)
        return AddIngredientViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onBindViewHolder(holder: AddIngredientViewHolder, position: Int) {
        holder.bindView(ingredients[position])
    }
}
