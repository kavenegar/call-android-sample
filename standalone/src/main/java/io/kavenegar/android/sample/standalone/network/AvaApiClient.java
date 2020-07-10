package io.kavenegar.android.sample.standalone.network;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.kavenegar.android.sample.standalone.models.Call;
import io.kavenegar.android.sample.standalone.models.CallSession;

import io.kavenegar.android.sample.standalone.models.Endpoint;

public class AvaApiClient {

    String baseURL;

    RequestQueue queue;
    Gson gson;
    String accessToken;

    public AvaApiClient(String baseURL, Context context) {
        this.queue = Volley.newRequestQueue(context);
        this.baseURL = baseURL;
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Endpoint.class, (JsonDeserializer<Endpoint>) (src, type, gsonContext) -> new Endpoint(src.getAsJsonObject()));
        builder.registerTypeAdapter(Call.class, (JsonDeserializer<Call>) (src, type, gsonContext) -> new Call(src.getAsJsonObject()));
        builder.registerTypeAdapter(CallSession.class, (JsonDeserializer<CallSession>) (src, type, gsonContext) -> new CallSession(src.getAsJsonObject()));

        this.gson = builder.create();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }


    private void execute(VolleyJsonRequest request) {
        if (this.getAccessToken() != null) {
            request.setAuthorization(this.getAccessToken());
        }
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 0, 1));
        queue.add(request);
    }


    public void call(String caller, String receptor, Callback<CallSession> onSuccess, Callback<ApiError> onError) {

        JsonObject json = new JsonObject();
        JsonObject callerUsernameJson = new JsonObject();
        callerUsernameJson.addProperty("username", caller);

        json.add("caller", callerUsernameJson);

        JsonObject receptorUsernameJson = new JsonObject();
        receptorUsernameJson.addProperty("username", receptor);
        json.add("receptor", receptorUsernameJson);

        VolleyJsonRequest request = new VolleyJsonRequest(Request.Method.POST, baseURL + "/calls", json.toString(), response -> {
            CallSession user = gson.fromJson(response, CallSession.class);
            onSuccess.accept(user);
        }, error -> onError.accept(new ApiError(error)));
        this.execute(request);
    }

    public void sendLog(Integer id, String logs, Callback<JsonObject> onSuccess, Callback<ApiError> onError) {

        Map<String, String> params = new HashMap<>();
        params.put("content", logs);

        VolleyJsonRequest request = new VolleyJsonRequest(Request.Method.POST, baseURL + "/calls/" + id + "/logs", "", response -> {
            JsonObject user = gson.fromJson(response, JsonObject.class);
            onSuccess.accept(user);
        }, error -> onError.accept(new ApiError(error)));
        this.execute(request);
    }

    public void getEndpoints(Callback<List<Endpoint>> onSuccess, Callback<ApiError> onError) {


        VolleyJsonRequest request = new VolleyJsonRequest(Request.Method.GET, baseURL + "/endpoints", "", response -> {
            JsonObject json = gson.fromJson(response, JsonObject.class);
            TypeToken token = new TypeToken<ArrayList<Endpoint>>() {
            };
            List<Endpoint> user = gson.fromJson(json.get("result"), token.getType());
            onSuccess.accept(user);
        }, error -> onError.accept(new ApiError(error)));
        this.execute(request);
    }

    public void getEndpoint(Integer id, Callback<Endpoint> onSuccess, Callback<ApiError> onError) {


        VolleyJsonRequest request = new VolleyJsonRequest(Request.Method.GET, baseURL + "/endpoints/" + id, "", response -> {
            Endpoint json = gson.fromJson(response, Endpoint.class);
            onSuccess.accept(json);
        }, error -> onError.accept(new ApiError(error)));
        this.execute(request);
    }

    public void getCalls(Callback<List<Call>> onSuccess, Callback<ApiError> onError) {

        Map<String, String> params = new HashMap<>();
        VolleyJsonRequest request = new VolleyJsonRequest(Request.Method.GET, baseURL + "/calls", "", response -> {
            JsonObject json = gson.fromJson(response, JsonObject.class);
            TypeToken token = new TypeToken<ArrayList<Call>>() {
            };
            List<Call> user = gson.fromJson(json.get("result"), token.getType());
            onSuccess.accept(user);
        }, error -> onError.accept(new ApiError(error)));
        this.execute(request);
    }

    public void getSessions(String username, Callback<List<CallSession>> onSuccess, Callback<ApiError> onError) {

        VolleyJsonRequest request = new VolleyJsonRequest(Request.Method.GET, baseURL + "/sessions?endpoint=" + username, "", response -> {
            JsonObject json = gson.fromJson(response, JsonObject.class);
            TypeToken token = new TypeToken<ArrayList<CallSession>>() {
            };
            List<CallSession> user = gson.fromJson(json.get("result"), token.getType());
            onSuccess.accept(user);
        }, error -> onError.accept(new ApiError(error)));
        this.execute(request);
    }

    public void reportCall(Integer callId, String description, Integer rating, String logs, Callback<JsonObject> onSuccess, Callback<ApiError> onError) {

        Map<String, String> params = new HashMap<>();
        params.put("callId", callId.toString());
        params.put("description", description);
        params.put("rating", rating.toString());
        params.put("logs", logs);
        VolleyJsonRequest request = new VolleyJsonRequest(Request.Method.POST, baseURL + "/reports", "", response -> {

            JsonObject user = gson.fromJson(response, JsonObject.class);
            onSuccess.accept(user);
        }, error -> onError.accept(new ApiError(error)));
        this.execute(request);
    }


}
