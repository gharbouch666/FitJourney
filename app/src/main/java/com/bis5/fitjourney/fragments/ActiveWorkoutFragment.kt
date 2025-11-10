package com.bis5.fitjourney.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bis5.fitjourney.adapters.ActiveExerciseAdapter
import com.bis5.fitjourney.databinding.FragmentActiveWorkoutBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory

class ActiveWorkoutFragment : Fragment() {

    private var _binding: FragmentActiveWorkoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var activeExerciseAdapter: ActiveExerciseAdapter
    private val args: ActiveWorkoutFragmentArgs by navArgs()
    private lateinit var chronometer: Chronometer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        setupToolbar()
        setupRecyclerView()

        workoutViewModel.getExercises(args.workoutId).observe(viewLifecycleOwner) { exercises ->
            activeExerciseAdapter.updateExercises(exercises)
        }

        binding.btnFinishWorkout.setOnClickListener {
            finishWorkout()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        // Add chronometer to toolbar
        chronometer = Chronometer(requireContext())
        binding.toolbar.addView(chronometer)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    private fun setupRecyclerView() {
        activeExerciseAdapter = ActiveExerciseAdapter {
            exerciseName, setNumber, weight, reps ->
            workoutViewModel.logSet(args.workoutLogId, exerciseName, setNumber, reps, weight)
        }
        binding.rvActiveExercises.adapter = activeExerciseAdapter
    }

    private fun finishWorkout() {
        chronometer.stop()
        workoutViewModel.finishWorkout(args.workoutLogId)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}