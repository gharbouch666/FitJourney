package com.bis5.fitjourney.fragments

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bis5.fitjourney.R
import com.bis5.fitjourney.databinding.FragmentHomeBinding
import com.bis5.fitjourney.models.AppDatabase
import com.bis5.fitjourney.models.SetLog
import com.bis5.fitjourney.models.Workout
import com.bis5.fitjourney.viewmodels.WorkoutViewModel
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel
    private var userEmail: String? = null
    private var selectedChartExercise = "Bench Press" // Default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        userEmail = requireActivity().intent.getStringExtra("USER_EMAIL")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val viewModelFactory = WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao())
        workoutViewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        userEmail?.let { email ->
            workoutViewModel.createDefaultWorkoutsIfNoneExist(email) // This is the new line

            binding.tvUserName.text = email.split("@").firstOrNull()?.replaceFirstChar(Char::titlecase) ?: "User"
            observeNextWorkout(email)
            observeWorkoutLogs(email)
            observeChartData(email)
        }

        binding.btnGoToWorkout.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWorkoutListFragment())
        }

        binding.ivSettings.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        binding.ivChartOptions.setOnClickListener {
            showExerciseSelectionDialog()
        }
    }

    private fun observeNextWorkout(email: String) {
        workoutViewModel.getWorkouts(email).observe(viewLifecycleOwner) { workouts ->
            if (workouts.isNotEmpty()) {
                val nextWorkout = workouts.maxByOrNull { it.date }!!
                binding.tvNextWorkoutName.text = nextWorkout.name
                updateNextWorkoutIcon(nextWorkout)
                binding.cardNextWorkout.setOnClickListener {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWorkoutDetailFragment(nextWorkout.id))
                }
            } else {
                binding.tvNextWorkoutName.text = "No workouts yet!"
                binding.ivNextWorkoutIcon.visibility = View.GONE
            }
        }
    }

    private fun observeWorkoutLogs(email: String) {
        workoutViewModel.getAllLogsForUser(email).observe(viewLifecycleOwner) { logs ->
            updateHeatmap(logs.map { it.dateStarted })
        }
    }

    private fun observeChartData(email: String) {
        workoutViewModel.getSetHistoryForExercise(email, selectedChartExercise).observe(viewLifecycleOwner) { setLogs ->
            if (setLogs.isNotEmpty()) {
                setupProgressChart(setLogs)
            }
        }
    }

    private fun showExerciseSelectionDialog() {
        userEmail?.let { email ->
            workoutViewModel.getUniqueExerciseNames(email).observe(viewLifecycleOwner) { exerciseNames ->
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select Exercise for Chart")
                builder.setItems(exerciseNames.toTypedArray()) { _, which ->
                    selectedChartExercise = exerciseNames[which]
                    binding.tvChartTitle.text = "Progress: $selectedChartExercise"
                    observeChartData(email) // Re-observe with the new exercise
                }
                builder.show()
            }
        }
    }

    private fun updateNextWorkoutIcon(workout: Workout) {
        val iconRes = when (workout.name.lowercase()) {
            "push day" -> R.drawable.push
            "pull day" -> R.drawable.pull
            "leg day" -> R.drawable.legs
            "upper body" -> R.drawable.upper
            "lower body" -> R.drawable.lower
            "core" -> R.drawable.core
            "cardio" -> R.drawable.cardio
            else -> R.drawable.dumbell
        }
        binding.ivNextWorkoutIcon.setImageResource(iconRes)
    }

    private fun setupProgressChart(setLogs: List<SetLog>) {
        val entries = ArrayList<Entry>()
        if (setLogs.isEmpty()) return

        val referenceDate = setLogs.first().timestamp
        setLogs.forEach { log ->
            val daysSinceStart = TimeUnit.MILLISECONDS.toDays(log.timestamp - referenceDate).toFloat()
            entries.add(Entry(daysSinceStart, log.weight.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Weight (kg)")
        val lineData = LineData(dataSet)

        binding.progressChart.data = lineData
        binding.progressChart.description.isEnabled = false
        binding.progressChart.invalidate() // refresh
    }

    private fun updateHeatmap(workoutDates: List<Long>) {
        binding.heatmapContainer.removeAllViews()
        val cal = Calendar.getInstance()

        for (i in 6 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dayView = View(context)
            val size = resources.getDimensionPixelSize(R.dimen.heatmap_day_size)
            val margin = resources.getDimensionPixelSize(R.dimen.heatmap_day_margin)
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(margin, 0, margin, 0)
            dayView.layoutParams = params

            val workoutOnThisDay = workoutDates.any { date ->
                val workoutCal = Calendar.getInstance()
                workoutCal.timeInMillis = date
                workoutCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && workoutCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
            }

            val circleDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.heatmap_day_circle)?.mutate() as GradientDrawable
            val colorRes = if (workoutOnThisDay) R.color.md_theme_primary else R.color.md_theme_surfaceVariant
            circleDrawable.setColor(ContextCompat.getColor(requireContext(), colorRes))
            dayView.background = circleDrawable

            binding.heatmapContainer.addView(dayView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
