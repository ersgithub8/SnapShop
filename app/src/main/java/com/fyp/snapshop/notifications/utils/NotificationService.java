package com.fyp.snapshop.notifications.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationService {

    private static NotificationService notificationService = null;
    private static Context context;

    private NotificationService(){

    }


    public static NotificationService getInstance(Context context){
        if (notificationService == null){
            NotificationService.context = context;
            notificationService = new NotificationService();
        }
        return notificationService;
    }


    public void sendNotification(String to, String title, String message){

        try {
            JSONObject notification = new JSONObject();
            JSONObject notificationBody = new JSONObject();
            notificationBody.put("body", message);
            notificationBody.put("title", title);
            notification.put("to", to);
            notification.put("notification", notificationBody);
            String FCM_API = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("VolleyResponse", "onResponse: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VolleyError : ", "onErrorResponse: Didn't work");
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "key=AAAAVL23_gE:APA91bF2pdfqKddcF9kOfmAoa8mYXZKN_HjiM6zU49JGyBv7wbhIaalfwca7UNe_ZOE_w73H6EBeEEhpcDXFCXvHw771Hp2BFKpIMMpsvFb8Cj7Ptg0Ht6NwWoKH2Qxe_SAj2Cf5YGCK");
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
