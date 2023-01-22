/*
 * Copyright © 2023 ShirayukiProject
 * Date: 22.01.2023
 * Time: 13.21
 * Author: ItzKaguya <mizuartana18@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itzkaguya.yukidashboard.fragments;

import android.provider.SearchIndexableResource;

import android.os.Bundle;
import android.content.Context;
import com.android.settings.R;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SearchIndexable
public class NotificationsSettings extends SettingsPreferenceFragment {

    private static final String LIGHTS_CATEGORY = "lights_category";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.notifications_settings);

        PreferenceCategory mLights = (PreferenceCategory) findPreference(LIGHTS_CATEGORY);
        if (!hasBatteryLights(getContext()))
            getPreferenceScreen().removePreference(mLights);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SHIRAYUKI;
    }

    private static boolean hasBatteryLights(Context context) {
        return context.getResources().getBoolean(
                com.android.internal.R.bool.config_intrusiveBatteryLed);
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.notifications_settings;
                    return Arrays.asList(sir);
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}