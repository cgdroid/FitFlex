package com.tmhnry.fitflex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tmhnry.fitflex.Diet;
import com.tmhnry.fitflex.adapter.FoodCategoriesAdapter;
import com.tmhnry.fitflex.databinding.FragmentDietCategoriesBinding;
import com.tmhnry.fitflex.model.Foods;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DietCategories#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DietCategories extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_DIET_CATEGORIES";
    FragmentDietCategoriesBinding b;

    private String mParam1;
    private String mParam2;

    private List<Foods.Category> categories;

    public DietCategories() {
    }

    public static DietCategories newInstance() {
        DietCategories fragment = new DietCategories();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentDietCategoriesBinding.inflate(inflater, container, false);

        categories = Foods.getCategories();

        FoodCategoriesAdapter adapter = new FoodCategoriesAdapter(getActivity(), listener, categories);

        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inflate the layout for this fragment
        return b.getRoot();
    }

    private final FoodCategoriesAdapter.Listener listener = new FoodCategoriesAdapter.Listener() {
        @Override
        public void onCardClick(int categoryId) {
            ((Diet) getActivity()).loadFragment(DietCategoryFoods.FRAGMENT_ID, categoryId);
        }
    };

//    @Override
//    public void openDialog(int position) {
//        loading = true;
//
//        if (imageResults != null) {
//            imageResults.clear();
//        }
//
//        imageDialog.findViewById(R.id.img_food).setVisibility(View.INVISIBLE);
//        imageDialog.findViewById(R.id.txt_searching_image).setVisibility(View.VISIBLE);
//        imageDialog.show();
//        StringBuilder sb = new StringBuilder();
//
//        Foods.Model model = foodModels.get(position);
//        sb.append(model.getName());
//
////        sb.append(" ");
////        sb.append(model.getCategory());
//
//        onImageSearch(model.getName().replaceAll(" ", "+") + "+" + model.getCategory().getName().replaceAll(" ", "+"));
//
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            imageDialog.setCancelable(true);
//        }, 3000);
//
//    }

}