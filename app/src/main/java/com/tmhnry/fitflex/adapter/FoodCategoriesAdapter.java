package com.tmhnry.fitflex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tmhnry.fitflex.model.Foods;
import com.tmhnry.fitflex.R;

import java.util.List;

public class FoodCategoriesAdapter extends RecyclerView.Adapter<FoodCategoriesAdapter.ViewHolder> {

    private Listener listener;
    private Context context;
    private List<Foods.Category> categories;

    public interface Listener {
        void onCardClick(int categoryId);
    }

    private static Listener getListenerFromContext(Context context){
        Listener listener;

        try {
            listener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement " + "FoodCategoriesAdapter.Listener");
        }

        return listener;
    }


    public FoodCategoriesAdapter(Context context, List<Foods.Category> categories) {
        this(context, getListenerFromContext(context), categories);
    }

    public FoodCategoriesAdapter(Context context, Listener listener, List<Foods.Category> categories) {
        this.context = context;
        this.listener = listener;
        this.categories = categories;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view;
        view = inflate.inflate(R.layout.holder_food_category, parent, false);

//        ViewHolder holder = new ViewHolder(view, viewType);
//        if (viewType == TYPE_ENTITY) {
//            holder.wrapper.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Entity entity = entities.get(holder.getAbsoluteAdapterPosition() - 1);
//                    Dialog dialog = new Dialog(context);
//                    dialog.setContentView(R.layout.layout_entity_dialog);
//                    TextView name = dialog.findViewById(R.id.txt_entity_name);
//                    name.setText(entity.getName());
//                    dialog.show();
//                }
//            });
//        }
        return new ViewHolder(view, viewType);
    }

    private int getCategorySize(Foods.Category category){
        return Foods.getModels(category).size();
    }

    private double getCategoryAverageCalories(Foods.Category category){
        double calories = 0;
        List<Foods.Model> models = Foods.getModels(category);
        for(Foods.Model model : models){
            calories += model.getCalories();
        }
        return calories / models.size();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int absPos = position;
        Foods.Category category = categories.get(absPos);

        holder.name.setText(category.getName());

        StringBuilder sb = new StringBuilder();

        sb.append(Foods.getModels(category).size());

//        holder.size.setText(sb.toString());

        holder.img.setImageResource(category.getImageId());

        holder.size.setText(String.valueOf(getCategorySize(category)));

        double categoryAverageCalories = getCategoryAverageCalories(category);

        String calories = String.format("%.2f", category.getAverageCalories());
        holder.calories.setText(calories);

        holder.card.setOnClickListener(view -> {
            listener.onCardClick(category.getCode());
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView size;
        TextView calories;
        CardView card;
        ImageView img;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_food_category_name);
            card = itemView.findViewById(R.id.card);
            img = itemView.findViewById(R.id.img_category);
            calories = itemView.findViewById(R.id.txt_category_ave_calories);
            size = itemView.findViewById(R.id.txt_category_size);
        }
    }

}

