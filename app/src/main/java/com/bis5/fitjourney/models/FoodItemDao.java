package com.bis5.fitjourney.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FoodItem foodItem);

    @Delete
    void delete(FoodItem foodItem);

    @Query("SELECT * FROM food_items")
    LiveData<List<FoodItem>> getAllFoodItems();

    // COMMAND CENTER: A new decree to get today's total calories.
    @Query("SELECT SUM(calories) FROM food_items WHERE date(timestamp / 1000, 'unixepoch') = date('now', 'localtime')")
    LiveData<Integer> getCaloriesForToday();
}
