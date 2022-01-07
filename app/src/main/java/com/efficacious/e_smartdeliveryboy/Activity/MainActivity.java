package com.efficacious.e_smartdeliveryboy.Activity;

import static com.airbnb.lottie.L.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.efficacious.e_smartdeliveryboy.Adapter.NewTaskAdapter;
import com.efficacious.e_smartdeliveryboy.R;
import com.efficacious.e_smartdeliveryboy.model.NewTaskData;
import com.efficacious.e_smartdeliveryboy.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    Switch btnSwitch;
    List<NewTaskData> newTaskData;
    NewTaskAdapter newTaskAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.USER_DATA_SHARED_PREF,0);
        String mobileNumber = sharedPreferences.getString(Constant.MOBILE_NUMBER,null);
        btnSwitch = findViewById(R.id.btnSwitch);
        firebaseFirestore = FirebaseFirestore.getInstance();
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true){
                    btnSwitch.setText("Online");
                }else {
                    btnSwitch.setText("Offline");
                }
                HashMap<String,Object> map = new HashMap<>();
                map.put("Status",String.valueOf(b));
                firebaseFirestore.collection("DeliveryBoy")
                        .document(mobileNumber).update(map);
            }
        });

        firebaseFirestore.collection("DeliveryBoy")
                .document(mobileNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String Status = task.getResult().getString("Status");
                    if (Status.equalsIgnoreCase("true")){
                        btnSwitch.setChecked(true);
                    }
                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("FCMToken",token);
                        firebaseFirestore.collection("DeliveryBoy")
                                .document(mobileNumber).update(map);
                    }
                });


        recyclerView = findViewById(R.id.recyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();
        newTaskData = new ArrayList<>();
        newTaskAdapter = new NewTaskAdapter(newTaskData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), newTaskAdapter.getItemCount());
        recyclerView.setAdapter(newTaskAdapter);
        firebaseFirestore.collection("Orders").whereEqualTo("DeliveryBoyId",mobileNumber)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
//                        String Id = doc.getDocument().getId();
                        NewTaskData mData = doc.getDocument().toObject(NewTaskData.class);
                        newTaskData.add(mData);
                        newTaskAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }
}