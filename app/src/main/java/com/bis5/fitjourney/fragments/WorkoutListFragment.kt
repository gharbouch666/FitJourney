package com.bis5.fitjourney.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bis5.fitjourney.adapters.WorkoutListAdapter
import com.bis5.fitjourney.databinding.FragmentWorkoutListBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.models.Workout
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory

class WorkoutListFragment : Fragment() {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var workoutListAdapter: WorkoutListAdapter
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        userEmail = requireActivity().intent.getStringExtra("USER_EMAIL")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        setupRecyclerView()

        userEmail?.let { email ->
            workoutViewModel.getWorkouts(email).observe(viewLifecycleOwner) { workouts ->
                workoutListAdapter.submitList(workouts)
            }
        }

        binding.fabAddWorkout.setOnClickListener { 
            val action = WorkoutListFragmentDirections.actionWorkoutListFragmentToCreateWorkoutFragment()
            findNavController().navigate(action) 
        }
    }

    private fun setupRecyclerView() {
        workoutListAdapter = WorkoutListAdapter(
            onItemClicked = { workout ->
                val action = WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutDetailFragment(workout.id)
                findNavController().navigate(action)
            },
            onLongItemClicked = { workout ->
                showEditDeleteDialog(workout)
            }
        )
        binding.rvWorkouts.apply {
            adapter = workoutListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showEditDeleteDialog(workout: Workout) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Action")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showEditWorkoutDialog(workout)
                    1 -> showDeleteConfirmationDialog(workout)
                }
            }
            .show()
    }

    private fun showEditWorkoutDialog(workout: Workout) {
        val editText = EditText(requireContext()).apply {
            setText(workout.name)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Workout Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    workoutViewModel.updateWorkoutName(workout.id, newName)
                    Toast.makeText(requireContext(), "Workout updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(workout: Workout) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Workout")
            .setMessage("Are you sure you want to delete '${workout.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                workoutViewModel.deleteWorkout(workout)
                Toast.makeText(requireContext(), "Workout deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
