/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yjh.rer.util;

import android.os.SystemClock;
import android.support.v4.util.ArrayMap;

import java.util.concurrent.TimeUnit;

public class RateLimiter<KEY> {
    private ArrayMap<KEY, Long> mTimestamps = new ArrayMap<>();
    private final long mTimeout;

    public RateLimiter(int timeout, TimeUnit timeUnit) {
        this.mTimeout = timeUnit.toMillis(timeout);
    }

    public synchronized boolean shouldFetch(KEY key) {
        Long lastFetched = mTimestamps.get(key);
        long now = now();
        if (lastFetched == null) {
            mTimestamps.put(key, now);
            return true;
        }
        if (now - lastFetched > mTimeout) {
            mTimestamps.put(key, now);
            return true;
        }
        return false;
    }

    private long now() {
        return SystemClock.uptimeMillis();
    }

    public synchronized void reset(KEY key) {
        mTimestamps.remove(key);
    }
}
