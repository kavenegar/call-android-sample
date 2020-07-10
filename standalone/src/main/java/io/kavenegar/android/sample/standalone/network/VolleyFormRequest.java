package io.kavenegar.android.sample.standalone.network;

import androidx.annotation.Nullable;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class VolleyFormRequest extends StringRequest {

    private final Map<String, String> params;
    private final Map<String, String> headers;

    public VolleyFormRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/x-www-form-urlencoded");
    }


    public void setAuthorization(String accessToken) {
        this.headers.put("Authorization", "Bearer " + accessToken);
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
}
