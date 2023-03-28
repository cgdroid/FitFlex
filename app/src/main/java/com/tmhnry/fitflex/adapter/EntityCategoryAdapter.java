package com.tmhnry.fitflex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tmhnry.fitflex.model.Entities;
import com.tmhnry.fitflex.model.Entity;
import com.tmhnry.fitflex.R;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class EntityCategoryAdapter extends RecyclerView.Adapter<EntityCategoryAdapter.ViewHolder> {

    private static int TYPE_HEAD = 0;
    private static int TYPE_ENTITY = 1;
    private EntityListener entityListener;
    private Context context;
    private Entities.Category category;
    private List<Entity> entities;

    public interface EntityListener {
        void openDialog(int position);
    }


    public EntityCategoryAdapter(Context context, Entities.Category category, List<Entity> entities) {
        this.context = context;
        try {
            entityListener = (EntityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement EntityListener");
        }
        this.entities = entities;
        this.category = category;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view;
        if (viewType == TYPE_HEAD) {
            view = inflate.inflate(R.layout.layout_entities_header, parent, false);
        } else {
            view = inflate.inflate(R.layout.layout_entity_wrapper, parent, false);
        }
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
        if (holder.viewType == TYPE_HEAD) {
            holder.img.setImageResource(category.getImg());
        } else {
            int absPos = position - 1;
            Entity entity = entities.get(absPos);
            holder.name.setText(entity.getName());

            if (entity.getType().equals(Entity.Type.TIMED)) {
                int duration = entity.getTypeCount();
                holder.typeCount.setText(String.format("%02d:%02d", duration / 60, duration % 60));
            }
            if (entity.getType().equals(Entity.Type.COUNT)) {
                holder.typeCount.setText("x" + entity.getTypeCount());
            }

            holder.wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entityListener.openDialog(absPos);
                }
            });
            String uri = "@drawable/" + entity.getImg();
            int gif = context.getResources().getIdentifier(uri, null, context.getPackageName());
            holder.gif.setImageResource(gif);
        }
    }

    @Override
    public int getItemCount() {
        return entities.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout wrapper;
        int viewType;
        ImageView img;
        GifImageView gif;
        TextView name, typeCount;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_HEAD) {
                img = itemView.findViewById(R.id.img_category);
                this.viewType = TYPE_HEAD;
            } else {
                wrapper = itemView.findViewById(R.id.wrapper_entity);
                gif = itemView.findViewById(R.id.gif_exercise);
                name = itemView.findViewById(R.id.text_exercise_name);
                typeCount = itemView.findViewById(R.id.text_type_count);
                this.viewType = TYPE_ENTITY;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEAD : TYPE_ENTITY;
    }
}
