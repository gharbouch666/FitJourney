package com.bis5.fitjourney.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.FoodItem;
import com.bis5.fitjourney.models.FoodItemDao;
import java.util.List;

public class NutritionViewModel extends AndroidViewModel {

    private final FoodItemDao foodItemDao;
    private final LiveData<List<FoodItem>> allFoodItems;

    public NutritionViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        foodItemDao = db.foodItemDao();
        allFoodItems = foodItemDao.getAllFoodItems();
    }

    public LiveData<List<FoodItem>> getAllFoodItems() {
        return allFoodItems;
    }

    public void insert(FoodItem foodItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> foodItemDao.insert(foodItem));
    }

    public void delete(FoodItem foodItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> foodItemDao.delete(foodItem));
    }
}
