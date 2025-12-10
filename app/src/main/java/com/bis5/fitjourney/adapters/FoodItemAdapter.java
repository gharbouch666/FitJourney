package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bis5.fitjourney.databinding.ItemFoodBinding;
import com.bis5.fitjourney.models.FoodItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {

    private List<FoodItem> foodItems = new ArrayList<>();
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(FoodItem foodItem);
    }

    public FoodItemAdapter(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        FoodItem currentItem = foodItems.get(position);
        holder.bind(currentItem, longClickListener);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
        notifyDataSetChanged();
    }

    static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodBinding binding;

        public FoodItemViewHolder(ItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final FoodItem foodItem, final OnItemLongClickListener listener) {
            binding.tvFoodName.setText(foodItem.getName());
            binding.tvFoodCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", foodItem.getCalories()));

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onItemLongClick(foodItem);
                    return true; // Consume the long click
                }
                return false;
            });
        }
    }
}
