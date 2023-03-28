package com.tmhnry.fitflex.fragment;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tmhnry.fitflex.adapter.FoodsAdapter;
import com.tmhnry.fitflex.databinding.FragmentDietCategoryFoodsBinding;
import com.tmhnry.fitflex.databinding.HeaderBottomSheetFoodsBinding;
import com.tmhnry.fitflex.databinding.TableNutritionalContentBinding;
import com.tmhnry.fitflex.model.Foods;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

public class DietCategoryFoods extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_DIET_CATEGORY_FOODS";
    private static final String CATEGORY_ID = "category-id";

    public DietCategoryFoods() {
    }

    private int categoryId;
    private FragmentDietCategoryFoodsBinding b;
    private List<Foods.Model> models;
    private BottomSheetBehavior bottomSheet;
    private boolean peakHeightUpdated;

    public static DietCategoryFoods init(int categoryId) {
        DietCategoryFoods fragment = new DietCategoryFoods();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(CATEGORY_ID);
        }
        models = Foods.getModels(categoryId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentDietCategoryFoodsBinding.inflate(inflater, container, false);

        FoodsAdapter adapter = new FoodsAdapter(getActivity(), listener, models);

        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        b.recyclerView.setOnClickListener(view -> {
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        peakHeightUpdated = false;
        bottomSheet = BottomSheetBehavior.from(b.bottomSheetFoods.bottomSheet);
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Inflate the layout for this fragment
        return b.getRoot();
    }

    private void updatePeekHeight() {
        if (peakHeightUpdated) return;

        Resources resources = getActivity().getResources();
        getActivity().getResources().getDisplayMetrics();

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50,
                resources.getDisplayMetrics()
        );

        bottomSheet.setPeekHeight((int) px);
        peakHeightUpdated = true;
    }

    private final FoodsAdapter.Listener listener = new FoodsAdapter.Listener() {
        @Override
        public void onItemClick(int position) {
            updatePeekHeight();

            Foods.Model model = models.get(position);

            TableNutritionalContentBinding bTable = b.bottomSheetFoods.table;
            HeaderBottomSheetFoodsBinding bHeader = b.bottomSheetFoods.headerBottomSheetFoods;

            double categoryAveCal = Foods.Category.getCategory(categoryId).getAverageCalories();
            double calories = model.getCalories();
            double relativeAmount = calories / categoryAveCal;

            String sRelativeAmount = "";

            if (relativeAmount < 0.33) {
                sRelativeAmount = "very low";
            } else if (relativeAmount < 0.66) {
                sRelativeAmount = "low amount";
            } else if (relativeAmount < 1.0 || relativeAmount < 1.33) {
                sRelativeAmount = "about average";
            } else if (relativeAmount < 2.0) {
                sRelativeAmount = "high amount";
            } else {
                sRelativeAmount = "very high";
            }

            bHeader.txtFoodName.setText(model.getName());

            bHeader.txtRelativeRanking.setText(sRelativeAmount);

            bHeader.txtServingSize.setText(model.getQuantity());

            bHeader.txtCaloriesPerServing.setText(String.format("%.0f", model.getCalories()));

            StringBuilder sbCatAveCal = new StringBuilder();
            sbCatAveCal.append("Products in this category have on average ");
            sbCatAveCal.append(String.format("%.0f", categoryAveCal));
            sbCatAveCal.append(" calories.");

            bHeader.txtCategoryAveCalories.setText(sbCatAveCal.toString());

            StringBuilder sbCal = new StringBuilder();
            sbCal.append(String.format("%.0f", model.getCalories()));
            sbCal.append(" cal");

            bTable.txtCaloriesPerServing.setText(sbCal.toString());
            bTable.txtCarbsPerServing.setText(gramSizeToString(model.getCarbs()));
            bTable.txtFiberPerServing.setText(gramSizeToString(model.getFiber()));
            bTable.txtProteinPerServing.setText(gramSizeToString(model.getProtein()));
            bTable.txtSaturatedFatPerServing.setText(gramSizeToString(model.getSaturatedFat()));
            bTable.txtTotalFatPerServing.setText(gramSizeToString(model.getFat()));
            bTable.txtTransFatPerServing.setText(gramSizeToString(Math.abs(model.getFat() - model.getSaturatedFat())));

            if (bottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                return;
            }

            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    };

    private String gramSizeToString(double grams){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%.0f", grams));
        sb.append("g");
        return sb.toString();
    }

}