package com.bis5.fitjourney.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bis5.fitjourney.databinding.FragmentCreateWorkoutBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory
import com.google.android.material.chip.Chip

class CreateWorkoutFragment : Fragment() {

    private var _binding: FragmentCreateWorkoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateWorkoutBinding.inflate(inflater, container, false)
        userEmail = requireActivity().intent.getStringExtra("USER_EMAIL")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        binding.btnSaveWorkout.setOnClickListener {
            saveWorkout()
        }
    }

    private fun saveWorkout() {
        val selectedChipId = binding.cgWorkoutTypes.checkedChipId
        val customName = binding.etWorkoutName.text.toString().trim()

        val workoutName = if (selectedChipId != View.NO_ID) {
            view?.findViewById<Chip>(selectedChipId)?.text.toString()
        } else {
            customName
        }

        if (workoutName.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please select or enter a workout name", Toast.LENGTH_SHORT).show()
            return
        }

        userEmail?.let {
            workoutViewModel.createWorkout(workoutName, it)
            Toast.makeText(requireContext(), "Workout '$workoutName' saved!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
