package com.bis5.fitjourney.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StepCounterViewModel extends ViewModel {

    private final MutableLiveData<Integer> stepCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> stepGoal = new MutableLiveData<>(10000); // Default goal

    public LiveData<Integer> getStepCount() {
        return stepCount;
    }

    public void setStepCount(int steps) {
        stepCount.setValue(steps);
    }

    public LiveData<Integer> getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int goal) {
        stepGoal.setValue(goal);
    }

}
