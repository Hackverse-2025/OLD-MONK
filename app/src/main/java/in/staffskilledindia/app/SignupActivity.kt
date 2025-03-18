package `in`.staffskilledindia.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var userTypeRadioGroup: RadioGroup
    private lateinit var jobSeekerRadioButton: RadioButton
    private lateinit var employerRadioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signupButton = findViewById(R.id.signupButton)
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup)
        jobSeekerRadioButton = findViewById(R.id.jobSeekerRadioButton)
        employerRadioButton = findViewById(R.id.employerRadioButton)

        signupButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter name, email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Determine user type from RadioButtons
            val userType = when (userTypeRadioGroup.checkedRadioButtonId) {
                R.id.jobSeekerRadioButton -> "jobSeeker"
                R.id.employerRadioButton -> "employer"
                else -> "jobSeeker" // Default to jobSeeker if none selected (shouldn't happen)
            }

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Signup success, update UI with the signed-in user's information
                        Toast.makeText(this, "Signup Successful.", Toast.LENGTH_SHORT).show()

                        // After successful signup, save additional user info to Firestore
                        val user = auth.currentUser
                        user?.let {
                            val userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "userType" to userType // Save user type to Firestore
                                // Add other default profile fields if needed upon signup
                            )
                            firestore.collection("users")
                                .document(user.uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    // Navigate to MainContainerActivity after successful signup and data save
                                    val intentMainContainer = Intent(this, MainContainerActivity::class.java)
                                    startActivity(intentMainContainer)
                                    finish() // Close SignupActivity
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to save user info: ${e.message}", Toast.LENGTH_SHORT).show()
                                    // Optionally handle failure, maybe stay on signup page
                                }
                        }


                    } else {
                        // If signup fails, display a message to the user.
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
} 