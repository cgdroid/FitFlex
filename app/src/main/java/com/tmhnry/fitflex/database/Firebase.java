package com.tmhnry.fitflex.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Firebase {

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "first-name";
    public static final String KEY_LAST_NAME = "last-name";

//    private static final String URL = "https://android-daily-fitness-139ee-default-rtdb.asia-southeast1.firebasedatabase.app";
//    private static final String PROPERTIES_USERS = "RsOIRYnXc6GUGboBXNYb";
//    private static final String TABLE_PROPERTIES = "table-properties";
    private static final String TABLE_USERS = "users";
//    private static final String TABLE_EMAILS = "emails";

    private static FirebaseAuth auth;
    private static FirebaseFirestore database;

    public static void init() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        if (database == null) {
            database = FirebaseFirestore.getInstance();
        }
    }

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static void logout(){
        auth.signOut();
    }

    public interface OnRegisterListener {
        public void onStart();

        public void onSuccess(Map<String, Object> userCredentials);

        public void onUserExists();

        public void onFailed();
    }

    public interface OnLoginListener {
        public void onStart();

        public void onSuccess(Map<String, Object> userCredentials);

        public void onFailed();
    }

    private static void addUserToDB(Map<String, Object> userCredentials, OnRegisterListener listener) {
        String email = (String) userCredentials.get(KEY_EMAIL);

        assert(userCredentials.containsKey(KEY_PASSWORD));
        String password = (String) userCredentials.get(KEY_PASSWORD);

        assert(userCredentials.containsKey(KEY_FIRST_NAME));
        String firstName = (String) userCredentials.get(KEY_FIRST_NAME);

        assert(userCredentials.containsKey(KEY_LAST_NAME));
        String lastName = (String) userCredentials.get(KEY_LAST_NAME);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onSuccess(userCredentials);
            } else {
                listener.onFailed();
            }
        });

        database.collection(TABLE_USERS).add(userCredentials);
    }

    private static void checkExistingUser(Map<String, Object> userCredentials, OnRegisterListener listener) {
        database.collection(TABLE_USERS).whereEqualTo(KEY_EMAIL, userCredentials.get(KEY_EMAIL)).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.size() != 0) {
                listener.onUserExists();
            } else {
                addUserToDB(userCredentials, listener);
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }

    public static void register(Map<String, Object> userCredentials, OnRegisterListener listener) {
        assert (auth != null);
        assert (database != null);
        assert (userCredentials.containsKey(KEY_EMAIL));
        listener.onStart();
        checkExistingUser(userCredentials, listener);
    }

    public static void login(Map<String, Object> userCredentials, OnLoginListener listener) {
        assert (auth != null);

        assert(userCredentials.containsKey(KEY_EMAIL));
        String email = (String) userCredentials.get(KEY_EMAIL);

        assert(userCredentials.containsKey(KEY_PASSWORD));
        String password = (String) userCredentials.get(KEY_PASSWORD);

        listener.onStart();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getUserCredentials(userCredentials, listener);
            } else {
                listener.onFailed();
            }
        });
    }

    private static void getUserCredentials(Map<String, Object> userCredentials, OnLoginListener listener){
        database.collection(TABLE_USERS).whereEqualTo(KEY_EMAIL, userCredentials.get(KEY_EMAIL)).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.size() != 0) {
                DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);

                userCredentials.put(KEY_FIRST_NAME, snapshot.get(KEY_FIRST_NAME));
                userCredentials.put(KEY_LAST_NAME, snapshot.get(KEY_LAST_NAME));

                listener.onSuccess(userCredentials);
            } else {
                listener.onFailed();
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }
}
