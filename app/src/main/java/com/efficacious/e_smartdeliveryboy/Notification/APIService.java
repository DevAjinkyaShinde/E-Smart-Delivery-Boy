package com.efficacious.e_smartdeliveryboy.Notification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAl7KbhY0:APA91bEERfIDZo1Rf5HVXloERMWpFakG4W9gc0SVdJzPI_tRKDGEDZ2iv0Y96q3uLYzI4vivfeLnZAkXhDg1NlMmAQy_bQ15j5M9eNTD-j4oIvPjJuhElbgS2dd9lHRjC0sgXGNlE0dB"
    })

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body NotificationSender body);

}
