package com.canteenautomation.famfood.Utilities;

import android.content.Context;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.canteenautomation.famfood.R;

import org.json.JSONObject;

public class VolleyUtility {

    private RequestQueue queue;
    private Context context;

    public VolleyUtility(Context context, String url){
        queue = Volley.newRequestQueue(context);
        this.context = context;

        final Helper helper = new Helper(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                helper.printToast(response.toString(),1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.printToast(error.toString(),0);
            }
        });

        queue.add(jsObjRequest);
    }


}
