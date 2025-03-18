package `in`.staffskilledindia.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val userList: List<User>,
    private val currentUserType: String? // Receive current user type
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val userEmailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        val userTypeTextView: TextView = itemView.findViewById(R.id.userTypeTextView)
        val joinButton: Button = itemView.findViewById(R.id.joinButton) // Get Join Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_card_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userNameTextView.text = currentUser.name
        holder.userEmailTextView.text = currentUser.email
        holder.userTypeTextView.text = currentUser.userType

        // Control Join Button visibility based on user types
        if (currentUserType == "jobSeeker" && currentUser.userType == "employer") {
            holder.joinButton.visibility = View.VISIBLE // Show Join button for employers to job seekers
        } else {
            holder.joinButton.visibility = View.GONE // Hide Join button otherwise
        }

        holder.joinButton.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Join button clicked for ${currentUser.name}", Toast.LENGTH_SHORT).show()
            // Implement your "Join" action here (e.g., start chat, send request, etc.)
        }
    }

    override fun getItemCount() = userList.size
} 