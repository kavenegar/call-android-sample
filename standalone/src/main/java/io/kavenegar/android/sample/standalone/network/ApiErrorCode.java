package io.kavenegar.android.sample.standalone.network;

public enum ApiErrorCode {
    UNPROCESSABLE_ENTITY,
    FORBIDDEN,
    NETWORK_UNAVAILABLE,
    SERVICE_UNAVAILABLE,
    INTERNAL_SERVER_ERROR;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}