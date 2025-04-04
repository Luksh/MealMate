package np.com.ismt.laxman.mealmate.ui.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import np.com.ismt.laxman.mealmate.MainActivity
import np.com.ismt.laxman.mealmate.databinding.FragmentProfileBinding
import np.com.ismt.laxman.mealmate.ui.auth.LoginActivity
import np.com.ismt.laxman.mealmate.ui.auth.RegisterActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLoginState()
        setUpViews()
    }

    private fun checkLoginState() {
        if (auth.currentUser != null) {
            // User is logged in
            binding.llNotLoggedIn.visibility = View.GONE
            binding.clLoggedIn.visibility = View.VISIBLE
            loadUserData()
        } else {
            // User is not logged in
            binding.llNotLoggedIn.visibility = View.VISIBLE
            binding.clLoggedIn.visibility = View.GONE
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.tvUserName.text = document.getString("name") ?: "User"
                    }
                }
        }
    }

    private fun setUpViews() {
        binding.mbLogin.setOnClickListener {
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }

        binding.mbRegister.setOnClickListener {
            startActivity(Intent(requireActivity(), RegisterActivity::class.java))
        }

        binding.mbLogOut.setOnClickListener {
            setUpLogOutButton()
        }
    }

    private fun setUpLogOutButton() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Alert")
            .setMessage("Are you sure want to logout?")
            .setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    logOut()
                })
            .setNegativeButton(
                "No",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
            .show()
    }

    private fun logOut() {
        auth.signOut()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
