/*
 * Copyright © 2023 ShirayukiProject
 * Date: 22.01.2023
 * Time: 13.21
 * Author: Reydesu53 <reyhandra2007@gmail.com>
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

import static android.os.UserHandle.USER_CURRENT;
import static android.os.UserHandle.USER_SYSTEM;

import android.content.Context;
import android.content.ContentResolver;
import android.content.om.IOverlayManager;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.settings.R;
import android.net.Uri;

import com.android.settings.dashboard.DashboardFragment;

import androidx.preference.PreferenceScreen;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.logging.nano.MetricsProto;

import com.itzkaguya.yukidashboard.preference.SystemSettingListPreference;
import com.android.settings.development.OverlayCategoryPreferenceController;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import android.provider.Settings;
import android.os.UserHandle;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class UiThemingSettings extends DashboardFragment implements OnPreferenceChangeListener {

    private static final String TAG = "UiThemingSettings";

    private String MONET_ENGINE_COLOR_OVERRIDE = "monet_engine_color_override";

    private ColorPickerPreference mMonetColor;

    private Handler mHandler;
    private IOverlayManager mOverlayManager;
    private IOverlayManager mOverlayService;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final PreferenceScreen screen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mMonetColor = (ColorPickerPreference) screen.findPreference(MONET_ENGINE_COLOR_OVERRIDE);
        int intColor = Settings.Secure.getInt(resolver, MONET_ENGINE_COLOR_OVERRIDE, Color.WHITE);
        String hexColor = String.format("#%08x", (0xffffff & intColor));
        mMonetColor.setNewPreviewColor(intColor);
        mMonetColor.setSummary(hexColor);
        mMonetColor.setOnPreferenceChangeListener(this);

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SHIRAYUKI;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.ui_theming;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMonetColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_COLOR_OVERRIDE, intHex);
            return true;
        }
        return false;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.ui_theming) {

            };
}
