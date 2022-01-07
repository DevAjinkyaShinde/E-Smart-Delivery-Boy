package com.efficacious.e_smartdeliveryboy.WebService;

import com.efficacious.e_smartdeliveryboy.model.GetFCMTokenResponse;
import com.efficacious.e_smartdeliveryboy.model.GetUserDetailResponse;
import com.efficacious.e_smartdeliveryboy.model.UpdateStatusDetails;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    @GET("Customer")
    Call<GetUserDetailResponse> getUserDetails(
            @Query("Command") String command,
            @Query("Res_id") String resId,
            @Query("MobileNo") String mobileNo
    );

    @POST("UpdateOrderStatus")
    Call<ResponseBody> updateStatus(
            @Query("Command") String command,
            @Body UpdateStatusDetails updateStatusDetails
    );

    @GET("GetFCM")
    Call<GetFCMTokenResponse> getFCMToken(
            @Query("Command") String command,
            @Query("Res_Id") String ResId,
            @Query("Status") String Status
    );
}
