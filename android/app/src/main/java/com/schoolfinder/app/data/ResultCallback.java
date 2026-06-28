package com.schoolfinder.app.data;

/** Simple success/error callback the UI uses to consume repository results. */
public interface ResultCallback<T> {
    void onSuccess(T data);

    void onError(String message);
}
