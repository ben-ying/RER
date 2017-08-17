package com.yjh.rer.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.LiveDataTestUtil;

import java.util.List;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource() {
        ResultType r = null;
        result.setValue(Resource.loading(r));
        final LiveData<ResultType> dbSource = loadFromDb();
        if (dbSource == null) {
            fetchFromNetwork(dbSource);
        } else {
            result.addSource(dbSource, new Observer<ResultType>() {
                @Override
                public void onChanged(@Nullable ResultType resultType) {
                    result.removeSource(dbSource);
                    if (shouldFetch(resultType)) {
                        fetchFromNetwork(dbSource);
                    } else {
                        result.addSource(dbSource, new Observer<ResultType>() {
                            @Override
                            public void onChanged(@Nullable ResultType resultType) {
                                result.setValue(Resource.success(resultType));
                            }
                        });
                    }
                }
            });
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        if (dbSource != null) {
            result.addSource(dbSource, new Observer<ResultType>() {
                @Override
                public void onChanged(@Nullable ResultType resultType) {
                    result.setValue(Resource.loading(resultType));
                }
            });
        }
        result.addSource(apiResponse, new Observer<ApiResponse<RequestType>>() {
            @Override
            public void onChanged(final @Nullable ApiResponse<RequestType> requestTypeApiResponse) {
                result.removeSource(apiResponse);
                result.removeSource(dbSource);
                //noinspection ConstantConditions
                if (requestTypeApiResponse.isSuccessful()) {
                    saveResultAndReInit(requestTypeApiResponse);
                } else {
                    onFetchFailed();
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(@Nullable ResultType resultType) {
                            Resource.error(requestTypeApiResponse.getErrorMessage(), resultType);
                        }
                    });
                }
            }
        });
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
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                result.addSource(loadFromDb(), new Observer<ResultType>() {
                    @Override
                    public void onChanged(@Nullable ResultType resultType) {
                        result.setValue(Resource.success(resultType));
                    }
                });
            }
        }.execute();
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // Called to get the cached data from the database
    @NonNull @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected void onFetchFailed() {
    }

    // returns a LiveData that represents the resource
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }
}
