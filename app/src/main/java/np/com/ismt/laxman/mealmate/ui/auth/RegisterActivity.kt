package np.com.ismt.laxman.mealmate.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import np.com.ismt.laxman.mealmate.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.mbRegister.setOnClickListener {
            val name = binding.tietName.text.toString().trim()
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading state
            binding.mbRegister.isEnabled = false
            binding.mbRegister.text = "Creating account..."

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.mbRegister.isEnabled = true
                    binding.mbRegister.text = "Register"

                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )

                        user?.let { firebaseUser ->
                            db.collection("users").document(firebaseUser.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "User data saved successfully")
                                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error saving user data", e)
                                    Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> "Email already registered"
                            is FirebaseAuthWeakPasswordException -> "Password is too weak"
                            else -> "Registration failed: ${task.exception?.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
} 