package com.bis5.fitjourney.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bis5.fitjourney.adapters.LoggedSetAdapter
import com.bis5.fitjourney.databinding.FragmentWorkoutSummaryBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutSummaryFragment : Fragment() {

    private var _binding: FragmentWorkoutSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var loggedSetAdapter: LoggedSetAdapter
    private val args: WorkoutSummaryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)
        binding.toolbar.title = "Workout Summary"

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        setupRecyclerView()

        workoutViewModel.getLoggedSets(args.workoutLogId).observe(viewLifecycleOwner) { loggedSets ->
            loggedSetAdapter.updateLoggedSets(loggedSets)

            // Calculate and display total volume
            val totalVolume = loggedSets.sumOf { it.weight * it.reps }
            binding.tvTotalVolume.text = "$totalVolume kg"

            // Display the date of the workout from the first logged set
            loggedSets.firstOrNull()?.let {
                // We need to fetch the WorkoutLog to get the date
                // For now, let's just use a placeholder.
                // In a future step, we can enhance this.
            }
        }
    }

    private fun setupRecyclerView() {
        loggedSetAdapter = LoggedSetAdapter()
        binding.rvLoggedSets.adapter = loggedSetAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
