package com.tmhnry.fitflex.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tmhnry.fitflex.model.Foods;
import com.tmhnry.fitflex.R;

import java.util.List;

public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.ViewHolder> {

    private Listener listener;
    private Context context;
    private List<Foods.Model> models;

    public interface Listener {
        void onItemClick(int position);
    }

    private static FoodsAdapter.Listener getListenerFromContext(Context context){
        FoodsAdapter.Listener listener;

        try {
            listener = (FoodsAdapter.Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement " + "FoodsAdapter.Listener");
        }

        return listener;
    }


    public FoodsAdapter(Context context, List<Foods.Model> models) {
        this(context, getListenerFromContext(context), models);
    }

    public FoodsAdapter(Context context, FoodsAdapter.Listener listener, List<Foods.Model> models) {
        this.context = context;
        this.listener = listener;
        this.models = models;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view;
        view = inflate.inflate(R.layout.holder_food, parent, false);
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


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int absPos = position;
        Foods.Model model = models.get(absPos);

        holder.name.setText(model.getName());

        StringBuilder sb = new StringBuilder();
        sb.append(model.getCategory().getName());

        holder.category.setText(sb.toString());

        holder.itemView.setOnClickListener(view -> {
            listener.onItemClick(absPos);
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView name;
        TextView category;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id .food_name);
            category = itemView.findViewById(R.id.food_category);
        }
    }

}

