package com.viewpoints.aischeduler.data.openapi;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class OpenApiContext {
    public static final String PUBLIC_DATA_PORTAL_SERVICE_KEY = "JGAWuvfiX%2FYEHPBDG8MDlLE3%2FnwAR8NAUwFqfnvWE0aeNhOXMYablfN0nPp004yOQ0eXm8y6gsE%2FinXMOj%2BrdA%3D%3D";
    public static final String KAKAO_REST_API_KEY = "ad06aa9afaeecec43b70cc1b136c041b";

    private static OpenApiContext instance;

    private static Context context;
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
}
