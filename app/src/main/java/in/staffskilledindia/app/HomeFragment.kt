package `in`.staffskilledindia.app
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var currentUserType: String? = null // Store current user type

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView)
        usersRecyclerView.layoutManager = LinearLayoutManager(context)

        userList = ArrayList()

        loadCurrentUserTypeAndUsers() // Load current user type first, then users
    }

    private fun loadCurrentUserTypeAndUsers() {
        val currentUserUid = auth.currentUser?.uid
        currentUserUid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        currentUserType = document.getString("userType") // Get and store current user type
                        userAdapter = UserAdapter(userList, currentUserType) // Initialize adapter with user type
                        usersRecyclerView.adapter = userAdapter
                        loadUsersFromFirestore() // Load users after getting current user type
                    } else {
                        loadUsersFromFirestore() // If current user type not found, load all users (or handle as needed)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomeFragment", "Error loading current user type", e)
                    loadUsersFromFirestore() // On failure, load all users (or handle as needed)
                }
        } ?: run {
            loadUsersFromFirestore() // If no current user, load all users (or handle as needed)
        }
    }

    private fun loadUsersFromFirestore() {
        val usersCollection = firestore.collection("users")
        val query = if (currentUserType == "jobSeeker") {
            usersCollection.whereEqualTo("userType", "employer") // Show only employers to job seekers
        } else {
            usersCollection // For employers or if user type is unknown, show all users (or adjust as needed)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                userList.clear()
                for (document in querySnapshot) {
                    // Ensure we don't add the current user to the list
                    if (document.id != auth.currentUser?.uid) {
                        val user = document.toObject(User::class.java)
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error loading users from Firestore", e)
                // Handle error, e.g., display a Toast message
            }
    }
} 