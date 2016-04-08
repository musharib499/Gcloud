package com.gcloud.gaadi.events;

/**
 * Created by ankit on 28/11/14.
 */
public class NetworkEvent {
    private NetworkError networkError;
    private boolean showFullPageError;

    public NetworkEvent(NetworkError networkError, boolean showFullPageError) {
        this.networkError = networkError;
        this.showFullPageError = showFullPageError;
    }

    public NetworkError getNetworkError() {
        return networkError;
    }

    public void setNetworkError(NetworkError networkError) {
        this.networkError = networkError;
    }

    public boolean isShowFullPageError() {
        return showFullPageError;
    }

    public void setShowFullPageError(boolean showFullPageError) {
        this.showFullPageError = showFullPageError;
    }

    public enum NetworkError {
        SLOW_CONNECTION,
        NO_INTERNET_CONNECTION
    }
}
