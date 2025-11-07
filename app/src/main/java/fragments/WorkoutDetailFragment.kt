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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bis5.fitjourney.R
import com.bis5.fitjourney.databinding.FragmentWorkoutDetailBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.models.Exercise
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory

class WorkoutDetailFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var exerciseAdapter: ExerciseAdapter
    private val args: WorkoutDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        setupRecyclerView()

        workoutViewModel.getWorkout(args.workoutId).observe(viewLifecycleOwner) { workout ->
            binding.tvWorkoutName.text = workout?.name ?: "Workout Details"
        }

        workoutViewModel.getExercises(args.workoutId).observe(viewLifecycleOwner) { exercises ->
            exerciseAdapter.updateExercises(exercises)
        }

        binding.btnAddExercise.setOnClickListener {
            showAddExerciseDialog()
        }
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter()
        binding.rvExercises.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseAdapter
        }
    }

    private fun showAddExerciseDialog() {
        val context = requireContext()
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_exercise, null)
        val etExerciseName = dialogView.findViewById<EditText>(R.id.etExerciseName)
        val etSets = dialogView.findViewById<EditText>(R.id.etSets)
        val etReps = dialogView.findViewById<EditText>(R.id.etReps)
        val etWeight = dialogView.findViewById<EditText>(R.id.etWeight)

        AlertDialog.Builder(context)
            .setTitle("Add New Exercise")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etExerciseName.text.toString().trim()
                val sets = etSets.text.toString().toIntOrNull() ?: 0
                val reps = etReps.text.toString().toIntOrNull() ?: 0
                val weight = etWeight.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty()) {
                    workoutViewModel.addExercise(args.workoutId, name, sets, reps, weight)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ExerciseAdapter(
    private var exercises: List<Exercise> = emptyList()
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val sets: TextView = itemView.findViewById(R.id.tvSets)
        val reps: TextView = itemView.findViewById(R.id.tvReps)
        val weight: TextView = itemView.findViewById(R.id.tvWeight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name
        holder.sets.text = "Sets: ${exercise.sets}"
        holder.reps.text = "Reps: ${exercise.reps}"
        holder.weight.text = "Weight: ${exercise.weight} kg"
    }

    override fun getItemCount() = exercises.size

    fun updateExercises(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}