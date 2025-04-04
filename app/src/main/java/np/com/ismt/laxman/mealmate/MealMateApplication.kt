package np.com.ismt.laxman.mealmate

import android.app.Application
import com.google.firebase.FirebaseApp

class MealMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 