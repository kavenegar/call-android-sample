package io.kavenegar.android.sample.standalone.network;

public enum ApiErrorCode {
    UNPROCESSABLE_ENTITY,
    NETWORK_UNAVAILABLE,
    SERVICE_UNAVAILABLE,
    INTERNAL_SERVER_ERROR;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}