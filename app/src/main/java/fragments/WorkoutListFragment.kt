package com.bis5.fitjourney.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bis5.fitjourney.R
import com.bis5.fitjourney.databinding.FragmentWorkoutListBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.models.Workout
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutListFragment : Fragment() {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var workoutAdapter: WorkoutAdapter
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        // We will get the email from the main activity's intent
        userEmail = requireActivity().intent.getStringExtra("USER_EMAIL")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()

        userEmail?.let {
            workoutViewModel.getWorkouts(it).observe(viewLifecycleOwner) { workouts ->
                workoutAdapter.updateWorkouts(workouts)
                binding.tvNoWorkouts.visibility = if (workouts.isEmpty()) View.VISIBLE else View.GONE
                binding.rvWorkouts.visibility = if (workouts.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        workoutAdapter = WorkoutAdapter(emptyList()) { workout ->
            val action = WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutDetailFragment(workout.id)
            findNavController().navigate(action)
        }
        binding.rvWorkouts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workoutAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnCreateWorkout.setOnClickListener {
            showCreateWorkoutDialog()
        }
    }

    private fun showCreateWorkoutDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Name Your Workout")

        val input = EditText(context)
        input.hint = "e.g., Morning Run, Chest Day"
        builder.setView(input)

        builder.setPositiveButton("Create") { dialog, _ ->
            val workoutName = input.text.toString().trim()
            if (workoutName.isNotEmpty()) {
                userEmail?.let { workoutViewModel.createWorkout(workoutName, it) }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class WorkoutAdapter(
    private var workouts: List<Workout>,
    private val onWorkoutClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.tvWorkoutName)
        val workoutDate: TextView = itemView.findViewById(R.id.tvWorkoutDate)
        val workoutDuration: TextView = itemView.findViewById(R.id.tvWorkoutDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutName.text = workout.name
        holder.workoutDate.text = "Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(workout.date))}"
        holder.workoutDuration.text = "Duration: ${workout.duration} min"

        holder.itemView.setOnClickListener {
            onWorkoutClick(workout)
        }
    }

    override fun getItemCount(): Int = workouts.size

    fun updateWorkouts(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged() // For simplicity. Consider DiffUtil later.
    }
}