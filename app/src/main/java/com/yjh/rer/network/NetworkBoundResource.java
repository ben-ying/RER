package com.yjh.rer.network;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.Objects;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final MediatorLiveData<Resource<ResultType>> mResult = new MediatorLiveData<>();

    @MainThread
    protected NetworkBoundResource() {
        mResult.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();

        mResult.addSource(dbSource, resultType -> {
            mResult.removeSource(dbSource);
            if (shouldFetch(resultType)) {
                fetchFromNetwork(dbSource);
            } else {
                mResult.addSource(dbSource, newData -> setValue(Resource.success(newData)));

            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(mResult.getValue(), newValue)) {
            mResult.setValue(newValue);
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        // start progress
        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        mResult.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        mResult.addSource(apiResponse, requestTypeApiResponse -> {
            mResult.removeSource(apiResponse);
            mResult.removeSource(dbSource);
            if (requestTypeApiResponse.isSuccessful()) {
                saveResultAndReInit(requestTypeApiResponse);
            } else {
                onFetchFailed();
                mResult.addSource(dbSource, newData -> setValue(
                        Resource.error(requestTypeApiResponse.getErrorMessage(), newData)));
            }
        });
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.getBody();
    }

    @MainThread
    private void saveResultAndReInit(final ApiResponse<RequestType> response) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                saveCallResult(response.getBody());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                final LiveData<ResultType> dbSource = loadFromDb();
//                mResult.addSource(dbSource, newData -> setValue(Resource.success(newData)));
                mResult.addSource(dbSource, newData -> {
                    // prevent updating insertTime to fire this observer.
                    mResult.removeSource(dbSource);
                    setValue(Resource.success(newData));
                });
            }
        }.execute();
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    private boolean shouldFetch(@Nullable ResultType data) {
        return true;
    }

    // Called to get the cached data from the database
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected void onFetchFailed() {
    }

    // returns a LiveData that represents the resource
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return mResult;
    }
}
