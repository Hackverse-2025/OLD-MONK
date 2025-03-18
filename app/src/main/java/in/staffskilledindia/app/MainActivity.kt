package `in`.staffskilledindia.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, go to MainContainerActivity (which hosts ProfileFragment)
            val intentMainContainer = Intent(this, MainContainerActivity::class.java)
            startActivity(intentMainContainer)
            finish() // Close MainActivity as MainContainerActivity is now the main screen
            return // Exit from onCreate to prevent setting up Login/Signup buttons
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)

        loginButton.setOnClickListener {
            // Start LoginActivity
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        }

        signupButton.setOnClickListener {
            // Start SignupActivity
            val intentSignup = Intent(this, SignupActivity::class.java)
            startActivity(intentSignup)
        }
    }
}