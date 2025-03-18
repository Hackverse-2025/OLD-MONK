package `in`.staffskilledindia.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var genderEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var designationEditText: EditText
    private lateinit var firmNameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var detailsTextView: TextView
    private lateinit var logoutButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        genderEditText = findViewById(R.id.genderEditText)
        ageEditText = findViewById(R.id.ageEditText)
        addressEditText = findViewById(R.id.addressEditText)
        designationEditText = findViewById(R.id.designationEditText)
        firmNameEditText = findViewById(R.id.firmNameEditText)
        saveButton = findViewById(R.id.saveButton)
        detailsTextView = findViewById(R.id.detailsTextView)
        logoutButton = findViewById(R.id.logoutButton)


        loadUserProfile() // Load profile data when activity starts

        saveButton.setOnClickListener {
            saveUserProfile()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
            finish() // Close ProfileActivity
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val gender = document.getString("gender") ?: ""
                        val age = document.getString("age") ?: ""
                        val address = document.getString("address") ?: ""
                        val designation = document.getString("designation") ?: ""
                        val firmName = document.getString("firmName") ?: ""

                        genderEditText.setText(gender)
                        ageEditText.setText(age)
                        addressEditText.setText(address)
                        designationEditText.setText(designation)
                        firmNameEditText.setText(firmName)

                        displayUserDetails(gender, age, address, designation, firmName) // Display in TextView as well
                    } else {
                        detailsTextView.text = "No profile details found. Please enter your details."
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    detailsTextView.text = "Error loading profile."
                }
        }
    }


    private fun saveUserProfile() {
        val userId = auth.currentUser?.uid ?: return // Get user ID or return if null
        val gender = genderEditText.text.toString()
        val age = ageEditText.text.toString()
        val address = addressEditText.text.toString()
        val designation = designationEditText.text.toString()
        val firmName = firmNameEditText.text.toString()

        val userProfile = hashMapOf(
            "gender" to gender,
            "age" to age,
            "address" to address,
            "designation" to designation,
            "firmName" to firmName
        )

        firestore.collection("users").document(userId)
            .set(userProfile) // Use set to create or overwrite document
            .addOnSuccessListener {
                Toast.makeText(this, "Profile details saved.", Toast.LENGTH_SHORT).show()
                displayUserDetails(gender, age, address, designation, firmName) // Update displayed details
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayUserDetails(gender: String, age: String, address: String, designation: String, firmName: String) {
        val details = """
            **Your Details:**
            Gender: $gender
            Age: $age
            Address: $address
            Designation: $designation
            Firm Name: $firmName
        """.trimIndent()
        detailsTextView.text = details
    }
} 