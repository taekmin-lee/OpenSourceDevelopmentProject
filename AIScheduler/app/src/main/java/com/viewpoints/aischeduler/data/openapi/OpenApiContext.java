package com.viewpoints.aischeduler.data.openapi;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.maps.GeoApiContext;

public class OpenApiContext {
    public static final String PUBLIC_DATA_PORTAL_SERVICE_KEY = "JGAWuvfiX%2FYEHPBDG8MDlLE3%2FnwAR8NAUwFqfnvWE0aeNhOXMYablfN0nPp004yOQ0eXm8y6gsE%2FinXMOj%2BrdA%3D%3D";
    public static final String KAKAO_REST_API_KEY = "ad06aa9afaeecec43b70cc1b136c041b";
    public static final String KAKAO_NAVTIVE_APP_KEY = "4d355b0545036f87962ef2a7ad0acde8";

    private static OpenApiContext instance;

    private Context context;
    private GeoApiContext geoApiContext;
    private RequestQueue requestQueue;

    private OpenApiContext(Context context) {
        this.context = context;
    }

    public static synchronized OpenApiContext getInstance(Context context) {
        if (instance == null) {
            instance = new OpenApiContext(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public GeoApiContext getGeoApiContext() {
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyCsrNVHJsJvsHR75IzPSn1lEF0c4T-pplI").build();
        }

        return geoApiContext;
    }
}
