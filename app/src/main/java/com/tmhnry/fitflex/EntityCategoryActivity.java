package com.tmhnry.fitflex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tmhnry.fitflex.adapter.EntityCategoryAdapter;
import com.tmhnry.fitflex.databinding.ActivityEntityCategoryBinding;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.tmhnry.fitflex.model.Entities;
import com.tmhnry.fitflex.model.Entity;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageView;

public class EntityCategoryActivity extends AppCompatActivity implements EntityCategoryAdapter.EntityListener {

    public static final String ROUTE_ID = "com.example.fitflex.ACTIVITY_ENTITY_CATEGORY";
    private ActivityEntityCategoryBinding binding;
    private Entities.Category category;
    private List<Entity> entities;
    private int dialogPosition;
    private Dialog entityDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEntityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Tools.hideSystemBars(this);

        final int categoryId = getIntent().getIntExtra(ROUTE_ID, Entities.Category.IMMUNITY_BOOSTER.getCode());
        category = Entities.Category.getCategory(categoryId);
        entities = Entities.getInstance().getEntities(category);
        binding.imgBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EntityCategoryActivity.this, MainActivity.class));
            }
        });

        binding.btnStartEntities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntityCategoryActivity.this, EntityActivity.class);
                int[] args = new int[4];
                args[0] = category.getCode();
                args[1] = Calendar.getInstance(TimeZone.getDefault()).getTime().hashCode();
                args[2] = 0;
                args[3] = entities.size();
                intent.putExtra(EntityActivity.ROUTE_ID + EntityActivity.ARG1, args);
                startActivity(intent);
            }
        });
//        ImageView categoryImage = findViewById(R.id.img_category);
//        categoryImage.setImageResource(category.getImg());

        RecyclerView recyclerView = binding.entityCategory.recyclerView;
        EntityCategoryAdapter entityCategoryAdapter = new EntityCategoryAdapter(this, category, entities);

        recyclerView.setAdapter(entityCategoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        entityDialog = new Dialog(this);
        entityDialog.setContentView(R.layout.layout_entity_dialog);

        binding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                entityDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    @Override
    public void openDialog(int position) {
        dialogPosition = position;
        Entity entity = entities.get(position);

        GifImageView mGif = entityDialog.findViewById(R.id.gif_entity_dialog);
        String uri = "@drawable/" + entity.getImg();
        int gif = getResources().getIdentifier(uri, null, getPackageName());
        mGif.setImageResource(gif);

        TextView vName = entityDialog.findViewById(R.id.txt_name_entity_dialog);
        vName.setText(entity.getName());

        TextView vDescription = entityDialog.findViewById(R.id.txt_description_entity_dialog);
        vDescription.setText(entity.getDescription());

        ImageView vPrev = entityDialog.findViewById(R.id.img_prev_entity_dialog);
        ImageView vNext = entityDialog.findViewById(R.id.img_next_entity_dialog);

        TextView vPosition = entityDialog.findViewById(R.id.txt_position);
        vPosition.setText(String.valueOf(position + 1));

        TextView vEntitiesLength = entityDialog.findViewById(R.id.txt_entities_length);
        vEntitiesLength.setText(String.format("/%d", entities.size()));

        vPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogPosition != 0) {

                    dialogPosition = dialogPosition - 1;
                    Entity nEntity = entities.get(dialogPosition);

                    vName.setText(nEntity.getName());
                    vDescription.setText(nEntity.getDescription());
                    vPosition.setText(String.valueOf(dialogPosition + 1));

                    String uri = "@drawable/" + nEntity.getImg();
                    int gif = getResources().getIdentifier(uri, null, getPackageName());
                    mGif.setImageResource(gif);
                }
            }
        });

        vNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogPosition != entities.size() - 1) {
                    dialogPosition = dialogPosition + 1;
                    Entity nEntity = entities.get(dialogPosition);

                    vName.setText(nEntity.getName());
                    vDescription.setText(nEntity.getDescription());
                    vPosition.setText(String.valueOf(dialogPosition + 1));

                    String uri = "@drawable/" + nEntity.getImg();
                    int gif = getResources().getIdentifier(uri, null, getPackageName());
                    mGif.setImageResource(gif);
                }
            }
        });

        entityDialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EntityCategoryActivity.this, MainActivity.class));
    }

}