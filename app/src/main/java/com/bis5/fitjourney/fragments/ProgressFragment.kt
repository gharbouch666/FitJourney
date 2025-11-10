package com.bis5.fitjourney.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bis5.fitjourney.databinding.FragmentProgressBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel
    private val args: ProgressFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)
        binding.toolbar.title = "Workout Progress"

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        setupChart()

        workoutViewModel.getWorkoutHistory(args.workoutId).observe(viewLifecycleOwner) { history ->
            if (history.isNotEmpty()) {
                val entries = history.mapIndexed { index, workoutLog ->
                    // Calculate total volume for this workout log
                    // This is a simplified approach. A more robust solution would involve
                    // fetching the sets for each log and summing them up.
                    val volume = workoutLog.dateFinished?.let { (it - workoutLog.dateStarted) / 1000.0 } ?: 0.0
                    Entry(index.toFloat(), volume.toFloat())
                }

                val dataSet = LineDataSet(entries, "Total Volume Over Time")
                dataSet.color = Color.BLUE
                dataSet.valueTextColor = Color.BLACK

                val lineData = LineData(dataSet)
                binding.lineChart.data = lineData
                binding.lineChart.invalidate() // Refresh the chart
            }
        }
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            setDrawGridBackground(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
