package com.tmhnry.fitflex.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.tmhnry.fitflex.Diet;
import com.tmhnry.fitflex.EntityCategoryActivity;
import com.tmhnry.fitflex.MainActivity;
import com.tmhnry.fitflex.MyDialog;
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.StillImageActivity;
import com.tmhnry.fitflex.databinding.ActivityMainBinding;
import com.tmhnry.fitflex.databinding.FragmentHomeBinding;
import com.tmhnry.fitflex.databinding.WorkoutTrackerBinding;
import com.tmhnry.fitflex.model.Entities;
import com.tmhnry.fitflex.model.User;

public class HomeFragment extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_HOME";
    private static final String IS_DEFAULT = "IS_DEFAULT";
    private String[] quotes;
    private boolean isDefault;

    private FragmentHomeBinding binding;
    private WorkoutTrackerBinding trackerBinding;
    private ActivityMainBinding activityMainBinding;
    private Button button;
    private CardView immunityBooster;
    private CardView bellyWorkout;
    private CardView stayInShape;
    private CardView bandWorkout;
    private MyDialog mDialogSetGoal;

    // Belly Dialog
    private Dialog dHBel;
    private TextView vDPos;
    private TextView vDNeg;
    private TextView vDMes;
    private TextView vDTit;


    private Listener mListener;
    private int trackerHeight;
    private int currentQuote;

    private void initBellyDialog() {
        dHBel = new Dialog(getActivity());
        dHBel.setContentView(R.layout.dialog_alert_notification);
        dHBel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vDPos = dHBel.findViewById(R.id.txt_positive);
        vDNeg = dHBel.findViewById(R.id.txt_negative);
        vDMes = dHBel.findViewById(R.id.txt_alert_message);
        vDTit = dHBel.findViewById(R.id.dialog_title);

        vDTit.setText("Oops!");
        vDPos.setText("Proceed");
        vDNeg.setText("Cancel");

        vDMes.setText("Sorry, this feature is only available for users with recorded belly types.\n\nTo have one, we ask you to take the front and side views of your belly. Do you wish to proceed?");

        vDPos.setOnClickListener(view -> {
            dHBel.dismiss();
            startActivity(new Intent(getActivity(), StillImageActivity.class));
            getActivity().finish();
        });

        vDNeg.setOnClickListener(view -> {
            dHBel.dismiss();
        });
    }


    public HomeFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment init(boolean isDefault) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_DEFAULT, isDefault);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isDefault = getArguments().getBoolean(IS_DEFAULT);
        }
        currentQuote = 0;
        quotes = new String[5];
        quotes[0] = "The hardest thing about exercise is to start doing it. Once you are doing exercise regularly, the hardest thing is to stop it.";
        quotes[1] = "Motivation is what gets you started. Habit is what keeps you going.";
        quotes[2] = "To enjoy the glow of good health, you must exercise.";
        quotes[3] = "Exercise not only changes your body, it changes your mind, your attitude, and your mood.";
        quotes[4] = "All progress takes place outside the comfort zone.";
    }


    private final MyDialog.DialogListener editGoalListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {
            EditText editText = (EditText) view;
            editText.setTextColor(getActivity().getColor(R.color.gray));

            editText.setTextSize(15f);
            ColorStateList color = ColorStateList.valueOf(getActivity().getColor(R.color.light_100));
            ViewCompat.setBackgroundTintList(editText, color);

            editText.setHint("How many hours do you plan to exercise this week?");

            int weeklyExerHours = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_EXERCISE_HOURS_PER_WEEK);

            if (weeklyExerHours == 0) {
                editText.setText("N/A");
                return;
            }

            editText.setText(String.valueOf(weeklyExerHours));

        }

        @Override
        public boolean onConfirm(View view) {
            EditText editText = (EditText) view;

            String sHours = editText.getText().toString().trim();
            boolean confirm = false;

            int weeklyExerHours = 0;

            try {
                weeklyExerHours = Integer.parseInt(sHours);

                if (weeklyExerHours < 0) {
                    throw new Exception();
                }

                MyPreferences.save(getActivity(), MyPreferences.PREF_EXERCISE_HOURS_PER_WEEK, weeklyExerHours);

                editText.setText(String.valueOf(weeklyExerHours));
                confirm = true;

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Cannot parse exercise hours", Toast.LENGTH_SHORT).show();
            }

            ((MainActivity) getActivity()).updateBottomNavigationCount();

            return confirm;
        }

        @Override
        public void onCancel(View view) {
            EditText editText = (EditText) view;
            int weeklyExerHours = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_EXERCISE_HOURS_PER_WEEK);

            editText.setText(String.valueOf(weeklyExerHours));
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HomeFragment.Listener");
        }
        initBellyDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        trackerBinding = binding.workoutTracker;
//        vTracker.setBackgroundResource(R.drawable.background_main_header);

        bindCardViews();
        addDifficultyDrawables();

        mDialogSetGoal = MyDialog.fromEdit(getActivity(), editGoalListener, "Weekly Goal");

        LinearLayout container100 = binding.weeklyGoalContainer;
        RelativeLayout wrapper100 = binding.emptyWrapper;

        binding.txtQuote.setText(quotes[currentQuote]);

        binding.btnSetGoal.setOnClickListener(view -> {
            if(currentQuote == 4){
                currentQuote = 0;
            }
            else {
                currentQuote += 1;
            }
            binding.txtQuote.setText(quotes[currentQuote]);
        });

        if((int) MyPreferences.load(getActivity(), MyPreferences.PREF_BELLY_TYPE) == -1){
            binding.boxBellyWorkoutDescription.setVisibility(View.GONE);
            binding.boxContentUnavailable.setVisibility(View.VISIBLE);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("x");
        int size = Entities.getInstance()
                .getEntities((int) MyPreferences.load(getActivity(), MyPreferences.PREF_BELLY_TYPE))
                .size();
        sb.append(size);
        sb.append("  FULL-BODY WORKOUTS");
        binding.txtBellyWorkoutSize.setText(sb.toString());

        ((MainActivity) getActivity()).setAccountImageVisibility(View.INVISIBLE);

        immunityBooster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EntityCategoryActivity.class);
                intent.putExtra(EntityCategoryActivity.ROUTE_ID, Entities.Category.IMMUNITY_BOOSTER.getCode());
                startActivity(intent);
            }
        });

        bellyWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((int) MyPreferences.load(getActivity(), MyPreferences.PREF_BELLY_TYPE)) == -1) {
                    dHBel.show();
                    return;
                }
                Intent intent = new Intent(getActivity(), EntityCategoryActivity.class);
                int iBelly = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_BELLY_TYPE);
                intent.putExtra(EntityCategoryActivity.ROUTE_ID, iBelly);
                startActivity(intent);
            }
        });

        stayInShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EntityCategoryActivity.class);
                intent.putExtra(EntityCategoryActivity.ROUTE_ID, Entities.Category.STAY_IN_SHAPE.getCode());
                startActivity(intent);
            }
        });

        bandWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EntityCategoryActivity.class);
                intent.putExtra(EntityCategoryActivity.ROUTE_ID, Entities.Category.BAND_WORKOUT.getCode());
                startActivity(intent);
            }
        });

        binding.cardDiet.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), Diet.class);
            startActivity(intent);
        });

        trackerBinding.txtTotalWorkouts.setText(Integer.toString(User.getUserData().getTotalWorkouts()));
        trackerBinding.txtTotalCalories.setText(Float.toString(User.getUserData().getTotalCalories()));

        String sDuration = "";
        int duration = (int) User.getUserData().getTotalDuration() / 1000;
        if (duration > 60) {
            sDuration = String.valueOf(duration / 60);
            trackerBinding.txtDuration.setText("MINUTES");
        } else {
            sDuration = String.valueOf(duration);
            trackerBinding.txtDuration.setText("SECONDS");
        }
        trackerBinding.txtTotalDuration.setText(sDuration);

        String sCalories = "";
        float calories = (float) User.getUserData().getTotalCalories();
        if (calories == 0f) {
            sCalories = String.format("%d", 0);
            trackerBinding.txtCalories.setText("CALORIES");
        } else if (calories > 1000f) {
            sCalories = String.format("%.2f", calories / 1000f);
            trackerBinding.txtCalories.setText("KCALS");
        } else {
            sCalories = String.format("%.2f", calories);
            trackerBinding.txtCalories.setText("CALORIES");
        }
        trackerBinding.txtTotalCalories.setText(sCalories);

        int workouts = (int) User.getUserData().getTotalWorkouts();
        trackerBinding.txtTotalWorkouts.setText(String.valueOf(workouts));

        ((MainActivity) getActivity()).setToolbarAlpha(0);
        ((MainActivity) getActivity()).setMajorTitleText("DAILY");
        ((MainActivity) getActivity()).setMinorTitleText(" FITNESS");
        ((MainActivity) getActivity()).setMajorTitleColor(Color.WHITE);
        ((MainActivity) getActivity()).setMinorTitleColor(Color.WHITE);

        trackerBinding.getRoot().setBackgroundResource(R.drawable.background_main_header);
        if (!isDefault) {
            trackerBinding.getRoot().setPadding(0, 32 + ((MainActivity) getActivity()).getToolbarHeight(), 0, 52);
        } else {
            binding.getRoot().post(new Runnable() {
                @Override
                public void run() {
                    trackerBinding.getRoot().setPadding(0, 32 + ((MainActivity) getActivity()).getToolbarHeight(), 0, 52);
                }
            });
        }

        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                                       int oldScrollY) {
                mListener.onScrollChange(trackerBinding.getRoot().getHeight(), scrollX, scrollY);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void bindCardViews() {
        immunityBooster = binding.immunityBooster;
        bellyWorkout = binding.bellyWorkout;
        stayInShape = binding.stayInShape;
        bandWorkout = binding.bandWorkout;
    }

    private void addDrawableToCard(Entities.Category category, int difficulty) {
        ImageView[] drawables = new ImageView[3];

        for (int i = 0; i < 3; i++) {
            ImageView vImg = new ImageView(getActivity());
            vImg.setImageResource(R.drawable.ic_baseline_bolt_24);

            if (i < difficulty) {
                vImg.setImageTintList(ColorStateList.valueOf(getActivity().getColor(R.color.light_100)));
            } else {
                vImg.setImageTintList(ColorStateList.valueOf(getActivity().getColor(R.color.gray)));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
            params.setMargins(0, 0, 0, 0);
            vImg.setLayoutParams(params);

            drawables[i] = vImg;
        }
        switch (category) {
            case STAY_IN_SHAPE:
                for (ImageView drawable : drawables) {
                    binding.stayInShapeDifficulty.addView(drawable);
                }
                break;
            case BAND_WORKOUT:
                for (ImageView drawable : drawables) {
                    binding.bandWorkoutDifficulty.addView(drawable);
                }
                break;
            default:
                for (ImageView drawable : drawables) {
                    binding.immunityBoosterDifficulty.addView(drawable);
                }
        }
    }

    private void addDifficultyDrawables() {

        Entities.Category[] categories = new Entities.Category[3];
        categories[0] = Entities.Category.IMMUNITY_BOOSTER;
        categories[1] = Entities.Category.STAY_IN_SHAPE;
        categories[2] = Entities.Category.BAND_WORKOUT;

        for (Entities.Category category : categories) {
            int difficulty = category.getDifficulty();
            addDrawableToCard(category, difficulty);
        }

    }

    public interface Listener {
        void onScrollChange(int trackerHeight, int scrollX, int scrollY);
    }
}