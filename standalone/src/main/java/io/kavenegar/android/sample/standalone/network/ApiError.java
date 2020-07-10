package io.kavenegar.android.sample.standalone.network;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ApiError {

    ApiErrorCode error;

    public ApiError(VolleyError error) {
        /*
         else if (error instanceof VolleyError) {
            this.status = error.getLocalizedMessage();
            this.error = error.getMessage();
            error.printStackTrace();
         */
        if (error instanceof NoConnectionError) {
            this.error = ApiErrorCode.NETWORK_UNAVAILABLE;
        } else if (error instanceof TimeoutError) {
            this.error = ApiErrorCode.NETWORK_UNAVAILABLE;
        } else {
            String responseBody = new String(error.networkResponse.data);
            JsonObject json = new JsonParser().parse(responseBody).getAsJsonObject();
            String errorRaw = json.get("status").getAsString();
            this.error = ApiErrorCode.valueOf(errorRaw.toUpperCase());

        }
    }


    public ApiErrorCode getError() {
        return error;
    }

    public String getMessage() {
        switch (error) {
            case INTERNAL_SERVER_ERROR: {
                return "Internal server error";
            }
            case NETWORK_UNAVAILABLE: {
                return "Network is unavailable, please check your connectivity !";
            }
            default:
                return "Can't find translation for error code : " + error.toString();
        }
    }


    @Override
    public String toString() {
        return getMessage();
    }
}
