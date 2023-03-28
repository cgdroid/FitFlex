package com.tmhnry.fitflex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyDialog {

    private static int DIALOG_SELECTION = 0;
    private static int DIALOG_EDIT = 1;
    private static int DIALOG_SPINNER = 2;
    private static int HIDE_NONE = 0;
    private static int HIDE_TITLE = 1;
    private static int HIDE_SUBTITLE = 2;

    private android.app.Dialog dialog;
    private DialogListener listener;

    public android.app.Dialog getDialog(){
        return dialog;
    }

    public interface DialogListener {
        public void onCreate(View view);

        public boolean onConfirm(View view);

        public void onCancel(View view);
    }

    private void showSubtitle(){
        dialog.findViewById(R.id.dialog_subtitle).setVisibility(View.VISIBLE);
    }

    private void hideTitle(){
        dialog.findViewById(R.id.dialog_title).setVisibility(View.GONE);
    }


    private MyDialog(Context context, DialogListener listener, View view, String title, String subtitle) {

        this.listener = listener;

        dialog = new android.app.Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Toast.makeText(context, String.valueOf(this.getDialog()), Toast.LENGTH_SHORT).show();

        dialog.setContentView(R.layout.dialog_main);

        listener.onCreate(view);

        ((TextView) dialog.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) dialog.findViewById(R.id.dialog_subtitle)).setText(subtitle);

        ((ViewGroup) dialog.findViewById(R.id.content)).addView(view);

        dialog.findViewById(R.id.txt_confirm).setOnClickListener(v -> {
            boolean confirm = listener.onConfirm(view);
            if (confirm) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.txt_cancel).setOnClickListener(v -> {
            dialog.dismiss();
            listener.onCancel(view);
        });

        dialog.setOnCancelListener(dialogInterface -> {
            dialog.dismiss();
            listener.onCancel(view);
        });
    }

    private MyDialog(Context context, DialogListener listener, View view, String title) {
        this(context, listener, view, title, "");
    }

    public static MyDialog custom(Context context, DialogListener listener, String subtitle) {

        LinearLayout layout = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        layout.setHorizontalGravity(Gravity.START);
        layout.setOrientation(LinearLayout.VERTICAL);

        MyDialog myDialog = new MyDialog(context, listener, layout, "", subtitle);

        myDialog.hideTitle();
        myDialog.showSubtitle();

        return myDialog;
    }

    public static MyDialog fromSpinner(Context context, DialogListener listener, String subtitle) {

        LinearLayout layout = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        layout.setVerticalGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        MyDialog myDialog = new MyDialog(context, listener, layout, "", subtitle);

        myDialog.hideTitle();
        myDialog.showSubtitle();

        return myDialog;
    }

    public static MyDialog fromSelection(Context context, DialogListener listener, String title) {
        RadioGroup radioGroup = new RadioGroup(context);
        return new MyDialog(context, listener, radioGroup, title);
    }

    public static MyDialog fromEdit(Context context, DialogListener listener, String title) {
        EditText editText = new EditText(context);
        return new MyDialog(context, listener, editText, title);
    }

    public void open() {
        dialog.show();
    }


//    public interface DialogListener {
//
//    }
//
//    public interface UniqueDialogListener extends DialogListener {}
//
//    public static Dialog fromSelection(){
//
//    }
//
//    public interface UniqueDialogListener {
//        RadioButton onCreateRadioButton(String string);
//
//        void onCheckedChange(int position);
//    }
//
//    public static class Unique {
//        private UniqueDialogListener listener;
//        private DialogFactory.Dialog dialog;
//        private List<String> options;
//        private String main;
//        private RadioGroup radioGroup;
//
//        public void addOption(String option) {
//            options.add(option);
//        }
//
//        public void update(String main, List<String> options) {
//            this.main = main;
//            this.options.clear();
//            this.options = options;
//        }
//
//        public void pop() {
//            dialog.dismiss();
//        }
//
//        public void update(String main) {
//            this.main = main;
//        }
//
//        public Unique(Context context, String main, List<String> options) {
//            this.main = main;
//            this.options = options;
//
//            try {
//                listener = (UniqueDialogListener) context;
//            } catch (ClassCastException e) {
//                throw new ClassCastException(context.toString() + "must implement UniqueDialogListener");
//            }
//
//            LinearLayout layout = new LinearLayout(context);
//            radioGroup = new RadioGroup(context);
//
//            for (int i = 0; i < options.size(); i++) {
//                RadioButton button = listener.onCreateRadioButton(options.get(i));
//                button.setId(i);
//                radioGroup.addView(button);
//                if (main.equals(options.get(i))) {
//                    Log.d("True: ", String.format("%d", i));
//                    radioGroup.check(i);
//                }
//            }
//
//            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                    listener.onCheckedChange(i);
//                }
//            });
//
//            layout.addView(radioGroup);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMarginStart(20);
//            layout.setLayoutParams(params);
//            layout.setVerticalGravity(Gravity.CENTER);
//            layout.setOrientation(LinearLayout.VERTICAL);
//
//            dialog = new DialogFactory.Dialog(layout);
//        }
//
//        public void show(FragmentManager manager, String tag) {
//            dialog.show(manager, tag);
//        }
//    }
}
