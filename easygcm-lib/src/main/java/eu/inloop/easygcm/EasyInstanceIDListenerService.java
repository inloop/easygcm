/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.inloop.easygcm;

import com.google.android.gms.iid.InstanceIDListenerService;

public class EasyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "EasyInstanceIDListenerService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        EasyGcm.Logger.d("Received token refresh broadcast");

        EasyGcm.removeRegistrationId(getApplicationContext());

        if (GcmUtils.checkCanAndShouldRegister(getApplicationContext())) {
            startService(GcmRegistrationService.createGcmRegistrationIntent(this));
        }
    }
}
