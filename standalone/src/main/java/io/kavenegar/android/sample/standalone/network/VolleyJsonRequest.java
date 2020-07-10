package io.kavenegar.android.sample.standalone.network;



import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class VolleyJsonRequest extends StringRequest {

    private final String payload;
    private final Map<String, String> headers;

    public VolleyJsonRequest(int method, String url, String payload, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.payload = payload;
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json");
    }


    public void setAuthorization(String accessToken) {
        this.headers.put("Authorization", "Bearer " + accessToken);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return payload.getBytes();
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
}
