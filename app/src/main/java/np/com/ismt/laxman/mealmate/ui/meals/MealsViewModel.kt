package np.com.ismt.laxman.mealmate.ui.meals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MealsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Meals Fragment"
    }
    val text: LiveData<String> = _text
}
