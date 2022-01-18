package com.efficacious.e_smartdeliveryboy.Adapter;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.efficacious.e_smartdeliveryboy.Activity.MainActivity;
import com.efficacious.e_smartdeliveryboy.Notification.APIService;
import com.efficacious.e_smartdeliveryboy.Notification.Client;
import com.efficacious.e_smartdeliveryboy.Notification.Data;
import com.efficacious.e_smartdeliveryboy.Notification.NotificationSender;
import com.efficacious.e_smartdeliveryboy.R;
import com.efficacious.e_smartdeliveryboy.WebService.RetrofitClient;
import com.efficacious.e_smartdeliveryboy.model.GetFCM;
import com.efficacious.e_smartdeliveryboy.model.GetFCMTokenResponse;
import com.efficacious.e_smartdeliveryboy.model.GetUserDetailResponse;
import com.efficacious.e_smartdeliveryboy.model.GetUserDetails;
import com.efficacious.e_smartdeliveryboy.model.NewTaskData;
import com.efficacious.e_smartdeliveryboy.model.UpdateStatusDetails;
import com.efficacious.e_smartdeliveryboy.util.Constant;
import com.efficacious.e_smartdeliveryboy.util.LocationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTaskAdapter extends RecyclerView.Adapter<NewTaskAdapter.ViewHolder>{

    List<NewTaskData> newTaskData;
    Context context;
    List<GetUserDetails> getUserDetails;
    public NewTaskAdapter(List<NewTaskData> newTaskData) {
        this.newTaskData = newTaskData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_task_view,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.totalAmount.setText("₹" + String.valueOf(Math.round(Float.parseFloat(newTaskData.get(position).getTotalBill()))));
        holder.mobileNumber.setText(newTaskData.get(position).getMobileNumber());
        holder.userName.setText(newTaskData.get(position).getUserName());

        //fetch address here
        try {
            Call<GetUserDetailResponse> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getUserDetails("select","1",newTaskData.get(position).getMobileNumber());
            call.enqueue(new Callback<GetUserDetailResponse>() {
                @Override
                public void onResponse(Call<GetUserDetailResponse> call, Response<GetUserDetailResponse> response) {
                    if (response.isSuccessful()){
                        getUserDetails = response.body().getGetUserDetails();
                        String Address = getUserDetails.get(0).getAddress1() + ", " + getUserDetails.get(0).getAddress2() + ", " + getUserDetails.get(0).getAddress3();
                        holder.deliveryAddress.setText(Address);

                        holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                convertAddress(Address);
//                                startLocationService();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<GetUserDetailResponse> call, Throwable t) {
                    Toast.makeText(context, "Api Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                HashMap<String,Object> map = new HashMap<>();
                map.put("OrderId",Integer.parseInt(newTaskData.get(position).getOrderId()));
                map.put("TimeStamp",System.currentTimeMillis());
                map.put("Status","Order Shipped");
                firebaseFirestore.collection("Orders").document(newTaskData.get(position).getOrderId())
                        .collection("OrderStatus")
                        .add(map);
                HashMap<String,Object> update = new HashMap<>();
                update.put("Status","On the way");
                firebaseFirestore.collection("Orders")
                        .document(newTaskData.get(position).getOrderId())
                        .update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Request Accept..", Toast.LENGTH_SHORT).show();
                            //send to notification to user
                            firebaseFirestore.collection("TakeAway").document(newTaskData.get(position).getOrderId())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String token = task.getResult().getString("FCMToken");
                                        String title = "Your order is on the way..";
                                        String msg = "Tap to view";
                                        String flag = "UserHistory";
                                        sendNotification(token,title,msg,flag);
                                    }
                                }
                            });
                            //send notification to manager
                            try {
                                Call<GetFCMTokenResponse> FCM_CALL = RetrofitClient
                                        .getInstance()
                                        .getApi()
                                        .getFCMToken("getFCM","1","Manager");

                                FCM_CALL.enqueue(new Callback<GetFCMTokenResponse>() {
                                    @Override
                                    public void onResponse(Call<GetFCMTokenResponse> call, Response<GetFCMTokenResponse> response) {
                                        List<GetFCM> getFCM = response.body().getGetFCM();
                                        String FCMToken = getFCM.get(0).getVchFcmToken();
                                        String Title = "Takeaway order #" + newTaskData.get(position).getOrderId() + " accept request";
                                        String Msg = "Tap to view";
                                        String flag = "TakeAwayHistory";
                                        sendNotification(FCMToken,Title,Msg,flag);
                                        newTaskData.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,newTaskData.size());
                                    }

                                    @Override
                                    public void onFailure(Call<GetFCMTokenResponse> call, Throwable t) {

                                    }
                                });
                            }catch (Exception e){ }
                            holder.btnCancel.setVisibility(View.GONE);
                            holder.btnAccept.setVisibility(View.GONE);
//                            holder.btnDirection.setVisibility(View.VISIBLE);
//                            holder.btnComplete.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                HashMap<String,Object> update = new HashMap<>();
                update.put("Status","Cancel");
                firebaseFirestore.collection("Orders")
                        .document(newTaskData.get(position).getOrderId())
                        .update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Request cancel..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Orders")
                .document(newTaskData.get(position).getOrderId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String status = task.getResult().getString("Status");
                    if (status.equalsIgnoreCase("On the way")){
                        holder.btnCancel.setVisibility(View.GONE);
                        holder.btnAccept.setVisibility(View.GONE);
                        holder.btnDirection.setVisibility(View.VISIBLE);
                        holder.btnComplete.setVisibility(View.VISIBLE);
                    }
                    if (status.equalsIgnoreCase("Complete order")){
                        holder.btnCancel.setVisibility(View.GONE);
                        holder.btnAccept.setVisibility(View.GONE);
                        holder.btnDirection.setVisibility(View.GONE);
                        holder.btnComplete.setVisibility(View.GONE);
                    }
                    if (status.equalsIgnoreCase("Request")){
                        holder.btnAccept.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        holder.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.confirm_id);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                Button btnConfirmId = dialog.findViewById(R.id.btnConfirm);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                EditText confirmId = dialog.findViewById(R.id.orderId);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnConfirmId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ConfirmId = confirmId.getText().toString().trim();
                        if (!TextUtils.isEmpty(ConfirmId)){
                            if (newTaskData.get(position).getOrderId().equalsIgnoreCase(ConfirmId)){
                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                firebaseFirestore.collection("TakeAway").document(newTaskData.get(position).getOrderId())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            String token = task.getResult().getString("FCMToken");
                                            String title = "Successfully delivered your order !!";
                                            String msg = "Tap to see";
                                            String flag = "UserHistory";
                                            sendNotification(token,title,msg,flag);
                                        }
                                    }
                                });

                                try {
                                    Call<GetFCMTokenResponse> FCM_CALL = RetrofitClient
                                            .getInstance()
                                            .getApi()
                                            .getFCMToken("getFCM","1","Manager");

                                    FCM_CALL.enqueue(new Callback<GetFCMTokenResponse>() {
                                        @Override
                                        public void onResponse(Call<GetFCMTokenResponse> call, Response<GetFCMTokenResponse> response) {
                                            List<GetFCM> getFCM = response.body().getGetFCM();
                                            String FCMToken = getFCM.get(0).getVchFcmToken();
                                            String Title = "Takeaway order #" + newTaskData.get(position).getOrderId() + " delivered.";
                                            String Msg = "Tap to view";
                                            String flag = "TakeAwayHistory";
                                            sendNotification(FCMToken,Title,Msg,flag);
                                        }

                                        @Override
                                        public void onFailure(Call<GetFCMTokenResponse> call, Throwable t) {

                                        }
                                    });
                                }catch (Exception e){ }

                                HashMap<String,Object> map = new HashMap<>();
                                map.put("OrderId",Integer.parseInt(newTaskData.get(position).getOrderId()));
                                map.put("TimeStamp",System.currentTimeMillis());
                                map.put("Status","Order Complete");
                                firebaseFirestore.collection("Orders").document(newTaskData.get(position).getOrderId())
                                        .collection("OrderStatus")
                                        .add(map);
                                HashMap<String, Object> update = new HashMap<>();
                                update.put("Status", "Complete order");
                                UpdateStatusDetails updateStatusDetails = new UpdateStatusDetails(Integer.parseInt(newTaskData.get(position).getOrderId()), Constant.CLOSE_STATUS);
                                try {
                                    Call<ResponseBody> call = RetrofitClient
                                            .getInstance()
                                            .getApi()
                                            .updateStatus("update", updateStatusDetails);

                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()){
                                                firebaseFirestore.collection("Orders")
                                                        .document(newTaskData.get(position).getOrderId())
                                                        .update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(context, "Task complete..", Toast.LENGTH_SHORT).show();
                                                            holder.btnComplete.setVisibility(View.GONE);
                                                            holder.btnDirection.setVisibility(View.GONE);
                                                            holder.btnAccept.setVisibility(View.GONE);
                                                            newTaskData.remove(position);
                                                            notifyItemRemoved(position);
                                                            notifyItemRangeChanged(position,newTaskData.size());
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(context, "Api Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }catch (Exception e){ }
                                dialog.dismiss();
                            }
                            else{
                                Toast.makeText(context, "Order id not match", Toast.LENGTH_SHORT).show();
                                confirmId.setError("Invalid");
                            }
                        }else {
                            confirmId.setError("Empty field");
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return newTaskData.size();
    }

    private void sendNotification(String token, String title, String msg,String flag) {
        Data data = new Data(title,msg,flag);
        NotificationSender notificationSender = new NotificationSender(data,token);

        APIService apiService = Client.getRetrofit().create(APIService.class);

        apiService.sendNotification(notificationSender).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void convertAddress(String address) {
        if (address != null && !address.isEmpty()) {
            try {
                Geocoder geocoder = new Geocoder(context);
                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q="+lat+"," +lng+"&mode=l"));
                    intent.setPackage("com.google.android.apps.maps");

                    if (intent.resolveActivity(context.getPackageManager())!=null){
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    } // end convertAddress

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImg;
        TextView userName,mobileNumber;
        TextView deliveryAddress;
        TextView totalAmount;
        Button btnDirection,btnCancel,btnAccept,btnComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profileImg);
            userName = itemView.findViewById(R.id.userName);
            mobileNumber = itemView.findViewById(R.id.mobileNumber);
            deliveryAddress = itemView.findViewById(R.id.deliveryAddress);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            btnDirection = itemView.findViewById(R.id.btnDirection);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
    }

}
