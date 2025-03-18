package `in`.staffskilledindia.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var nameEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var designationEditText: EditText
    private lateinit var firmNameEditText: EditText
    private lateinit var logoutButton: Button
    private lateinit var saveButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nameEditText = view.findViewById(R.id.nameEditText)
        genderEditText = view.findViewById(R.id.genderEditText)
        ageEditText = view.findViewById(R.id.ageEditText)
        addressEditText = view.findViewById(R.id.addressEditText)
        designationEditText = view.findViewById(R.id.designationEditText)
        firmNameEditText = view.findViewById(R.id.firmNameEditText)
        logoutButton = view.findViewById(R.id.logoutButton)
        saveButton = view.findViewById(R.id.saveButton)

        loadUserProfile() // Load profile data when fragment is created

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Logged out.", Toast.LENGTH_SHORT).show()
            // Navigate to MainActivity after logout
            val intentLogin = Intent(context, MainActivity::class.java)
            startActivity(intentLogin)
            activity?.finish() // Close MainContainerActivity
        }

        saveButton.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        currentUser?.uid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userName = document.getString("name") ?: ""
                        val userGender = document.getString("gender") ?: ""
                        val userAge = document.getString("age") ?: ""
                        val userAddress = document.getString("address") ?: ""
                        val userDesignation = document.getString("designation") ?: ""
                        val userFirmName = document.getString("firmName") ?: ""


                        nameEditText.setText(userName)
                        genderEditText.setText(userGender)
                        ageEditText.setText(userAge)
                        addressEditText.setText(userAddress)
                        designationEditText.setText(userDesignation)
                        firmNameEditText.setText(userFirmName)

                    } else {
                        Toast.makeText(context, "Profile data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Error loading profile", e)
                }
        }
    }

    private fun saveUserProfile() {
        val userName = nameEditText.text.toString()
        val userGender = genderEditText.text.toString()
        val userAge = ageEditText.text.toString()
        val userAddress = addressEditText.text.toString()
        val userDesignation = designationEditText.text.toString()
        val userFirmName = firmNameEditText.text.toString()

        val currentUser = auth.currentUser
        currentUser?.uid?.let { uid ->
            val userMap = hashMapOf(
                "name" to userName,
                "gender" to userGender,
                "age" to userAge,
                "address" to userAddress,
                "designation" to userDesignation,
                "firmName" to userFirmName
            )

            firestore.collection("users")
                .document(uid)
                .update(userMap as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Error updating profile", e)
                }
        }
    }
} 