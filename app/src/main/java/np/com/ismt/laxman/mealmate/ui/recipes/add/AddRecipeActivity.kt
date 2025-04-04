package np.com.ismt.laxman.mealmate.ui.recipes.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import np.com.ismt.laxman.mealmate.R
import np.com.ismt.laxman.mealmate.databinding.ActivityAddRecipeBinding
import np.com.ismt.laxman.mealmate.helpers.HelperUtil
import np.com.ismt.laxman.mealmate.helpers.loadThumbnailImage
import np.com.ismt.laxman.mealmate.models.Ingredient
import np.com.ismt.laxman.mealmate.models.NutritionValue
import np.com.ismt.laxman.mealmate.models.Recipe
import np.com.ismt.laxman.mealmate.ui.camera.CustomCameraActivity

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRecipeBinding
    private var ingredients = mutableListOf<Ingredient>()
    private var instructions = mutableListOf<String>()
    private var imageUriPath = ""

    private lateinit var ingredientsAdapter: AddIngredientAdapter
    private lateinit var instructionAdapter: AddInstructionAdapter
    private lateinit var db: FirebaseFirestore

    private val startCustomCameraActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CustomCameraActivity.CAMERA_ACTIVITY_SUCCESS_RESULT_CODE) {
            imageUriPath = it.data?.getStringExtra(CustomCameraActivity.CAMERA_ACTIVITY_OUTPUT_FILE_PATH)!!
            binding.ivRecipeImage.loadThumbnailImage(this, imageUriPath)
        } else {
            imageUriPath = "";
            binding.ivRecipeImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private val startGalleryActivityForResult = registerForActivityResult(
        ActivityResultContracts.OpenDocument()) {
        if (it != null) {
            imageUriPath = it.toString()
            contentResolver.takePersistableUriPermission(
                Uri.parse(imageUriPath),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            binding.ivRecipeImage.loadThumbnailImage(this, imageUriPath)
        } else {
            imageUriPath = "";
            binding.ivRecipeImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    companion object {
        const val RESULT_CODE_COMPLETE = 1001
        const val RESULT_CODE_CANCEL = 1002
        const val GALLERY_PERMISSION_REQUEST_CODE = 11
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        setupViews()
    }

    private fun setupViews() {
        setUpBackButton()
        setUpAddImageButton()
        setUpCategory()
        setUpRecyclerViews()
        setUpAddNewIngredientButton()
        setUpAddNewInstructionsButton()
        setUpAddRecipeButton()
    }

    private fun setUpBackButton() {
        binding.mbBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpAddImageButton() {
        binding.btnSelectImage.setOnClickListener {
            handleOnAddImagebutton()
        }
    }

    private fun setUpCategory() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.recipe_category)
        )
        binding.actvRecipeCategory.setAdapter(adapter)
    }

    private fun setUpRecyclerViews() {
        setUpAddIngredientsRecyclerView()
        setUpAddInstructionsRecyclerView()
    }

    private fun setUpAddNewIngredientButton() {
        binding.mbAddMoreIngredient.setOnClickListener {
            ingredients.add(Ingredient())
            ingredientsAdapter.notifyItemInserted(ingredients.size - 1)
            binding.rvAddIngredient.smoothScrollToPosition(ingredients.size - 1)
        }
    }

    private fun setUpAddNewInstructionsButton() {
        binding.mbAddMoreInstruction.setOnClickListener {
            instructions.add("")
            instructionAdapter.notifyItemInserted(instructions.size - 1)
            binding.rvAddInstruction.smoothScrollToPosition(instructions.size - 1)
        }
    }

    private fun setUpAddRecipeButton() {
        binding.mbAddRecipe.setOnClickListener {
            validateInputData()
        }
    }

    private fun setUpAddIngredientsRecyclerView() {
        ingredients.add(Ingredient())
        ingredientsAdapter = AddIngredientAdapter(
            this.ingredients,
            onRemoveIngredient = { position ->
                ingredients.removeAt(position)
                ingredientsAdapter.notifyItemRemoved(position)
                ingredientsAdapter.notifyItemRangeChanged(position, ingredients.size)
            }
        )

        binding.rvAddIngredient.apply {
            layoutManager = LinearLayoutManager(this@AddRecipeActivity)
            adapter = ingredientsAdapter
        }
    }

    private fun setUpAddInstructionsRecyclerView() {
        instructions.add("")
        instructionAdapter = AddInstructionAdapter(
            this.instructions,
            onRemoveInstruction = { position ->
                instructions.removeAt(position)
                instructionAdapter.notifyItemRemoved(position)
                instructionAdapter.notifyItemRangeChanged(position, instructions.size)
            }
        )

        binding.rvAddInstruction.apply {
            layoutManager = LinearLayoutManager(this@AddRecipeActivity)
            adapter = instructionAdapter
        }
    }

    private fun handleOnAddImagebutton() {
        val pickImageBottomSheetDialog = BottomSheetDialog(this)
        pickImageBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_pick_image)

        val linearLayoutPickByCamera: LinearLayout = pickImageBottomSheetDialog
            .findViewById(R.id.ll_pick_by_camera)!!
        val linearLayoutPickByGallery: LinearLayout = pickImageBottomSheetDialog
            .findViewById(R.id.ll_pick_by_gallery)!!

        linearLayoutPickByCamera.setOnClickListener {
            pickImageBottomSheetDialog.dismiss()
            startCameraActivity()
        }
        linearLayoutPickByGallery.setOnClickListener {
            pickImageBottomSheetDialog.dismiss()
            startGalleryToPickImage()
        }

        pickImageBottomSheetDialog.setCancelable(true)
        pickImageBottomSheetDialog.show()
    }

    private fun startCameraActivity() {
        val intent = Intent(this, CustomCameraActivity::class.java)
        startCustomCameraActivityForResult.launch(intent)
    }

    private fun startGalleryToPickImage() {
        if (allPermissionForGalleryGranted()) {
            startActivityForResultFromGalleryToPickImage()
        } else {
            requestPermissions(
                getPermissionsRequiredForCamera().toTypedArray(),
                GALLERY_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun allPermissionForGalleryGranted(): Boolean {
        var granted = false
        for (permission in getPermissionsRequiredForCamera()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }
        }
        return granted
    }

    private fun getPermissionsRequiredForCamera(): List<String> {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permissions
    }

    private fun startActivityForResultFromGalleryToPickImage() {
        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
//        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startGalleryActivityForResult.launch(arrayOf("image/*"))
    }

    private fun validateInputData() {
        if (isRecipeNameAbsent()) {
            HelperUtil.showToastMessage(this.applicationContext, "Please add a valid recipe name")
        } else if (isCategoryAbsent()) {
            HelperUtil.showToastMessage(this.applicationContext, "Please add a valid category")
        } else if (isCookingTimeAbsent()) {
            HelperUtil.showToastMessage(this.applicationContext, "Please add a valid cooking time")
        } else if (isDescriptionAbsent()) {
            HelperUtil.showToastMessage(this.applicationContext, "Please add a valid description")
        } else if (isIngredientAbsent()) {
            HelperUtil.showToastMessage(this.applicationContext, "Please add a valid ingredient")
        } else if (isInstructionAbsent()) {
            HelperUtil.showToastMessage(this.applicationContext, "Please add a valid instruction")
        } else if (isNutritionValueAbsent()) {
            HelperUtil.showToastMessage(this.applicationContext, "Please add a valid nutrition value")
        } else {
            addRecipeToDb()
        }
    }

    private fun isRecipeNameAbsent(): Boolean {
        return binding.tietRecipeName.text?.trim()?.isBlank() ?: true
    }

    private fun isCategoryAbsent(): Boolean {
        return binding.actvRecipeCategory.text.toString().isBlank()
    }

    private fun isCookingTimeAbsent(): Boolean {
        return binding.tietCookingTime.text?.trim()?.isBlank() ?: false
    }

    private fun isDescriptionAbsent(): Boolean {
        return binding.tietRecipeDescription.text?.trim()?.isBlank() ?: false
    }

    private fun isIngredientAbsent(): Boolean {
        ingredients.forEach { ingredient ->  
            if (ingredient.name.isBlank() ||
                ingredient.quantity.isBlank() ||
                ingredient.unit.isBlank()) {
                return true
            }
        }
        return false
    }

    private fun isInstructionAbsent(): Boolean {
        instructions.forEach { instruction ->
            if (instruction.isBlank()) {
                return true
            }
        }
        return false
    }
    
    private fun isNutritionValueAbsent(): Boolean {
        val carbs = binding.tietCarbs.text?.trim().toString()
        val protein = binding.tietProtein.text?.trim().toString()
        val calories = binding.tietCalories.text?.trim().toString()
        
        return (carbs.isBlank() || protein.isBlank() || calories.isBlank())
    }

    private fun addRecipeToDb() {
        val name = binding.tietRecipeName.text!!.trim().toString()
        val category = binding.actvRecipeCategory.text.toString()
        val cookingTime = binding.tietCookingTime.text!!.trim().toString()
        val description = binding.tietRecipeDescription.text!!.trim().toString()
        val carbs = binding.tietCarbs.text!!.trim().toString()
        val protein = binding.tietProtein.text!!.trim().toString()
        val calories = binding.tietCalories.text!!.trim().toString()
        val image = HelperUtil.imageUriToBitmapEncodedBase64(this, imageUriPath)

        val recipe = Recipe(
            image = image,
            name = name,
            category = category,
            cookingTime =  cookingTime,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            nutritionValue = NutritionValue(carbs, protein, calories),
            isMyRecipe = true
        )

        val newDocument = db.collection("recipes").document()
        recipe.id = newDocument.id

        newDocument
            .set(recipe)
            .addOnSuccessListener {
                Log.d("AddRecipeActivity", "Recipe : " + recipe.name + ", added")
                finish()
            }
            .addOnFailureListener { exception ->
                Log.d("AddRecipeActivity", "Fail to add recipe: " + recipe.name)
                exception.printStackTrace()
            }
    }
}
