/*
 * Copyright (C) 2020 Yet Another AOSP Project
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
package com.itzkaguya.yukidashboard.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.itzkaguya.yukidashboard.preference.SystemSettingListPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

@SearchIndexable
public class EdgeLightningSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static String KEY_COLOR_MODE = "edge_light_color_mode";
    private static String KEY_COLOR = "edge_light_custom_color";

    private SystemSettingListPreference mColorModePref;
    private ColorPickerPreference mColorPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_edge_lightning);
        final ContentResolver resolver = getContentResolver();
        final String accentColor = String.format("#%06X", (0xFFFFFF & getAccentColor()));
        mColorPref = (ColorPickerPreference) findPreference(KEY_COLOR);
        String colorHex = Settings.System.getStringForUser(resolver,
                KEY_COLOR, UserHandle.USER_CURRENT);

        // check for empty string on first boot
        if (TextUtils.isEmpty(colorHex)) {
            colorHex = accentColor;
        }

        mColorPref.setDefaultColor(getAccentColor());
        if (colorHex.toUpperCase().equals(accentColor.toUpperCase())) {
            mColorPref.setSummary(R.string.default_string);
        } else {
            mColorPref.setSummary(colorHex);
        }
        mColorPref.setNewPreviewColor(Color.parseColor(colorHex));
        mColorPref.setOnPreferenceChangeListener(this);

        mColorModePref = (SystemSettingListPreference) findPreference(KEY_COLOR_MODE);
        int value = Settings.System.getIntForUser(resolver,
                KEY_COLOR_MODE, 0, UserHandle.USER_CURRENT);
        mColorModePref.setValue(Integer.toString(value));
        mColorModePref.setSummary(mColorModePref.getEntry());
        mColorModePref.setOnPreferenceChangeListener(this);
        mColorPref.setEnabled(value == 3);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SHIRAYUKI;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getContentResolver();
        if (preference == mColorModePref) {
            int value = Integer.valueOf((String) newValue);
            int index = mColorModePref.findIndexOfValue((String) newValue);
            mColorModePref.setSummary(mColorModePref.getEntries()[index]);
            Settings.System.putIntForUser(resolver,
                    KEY_COLOR_MODE, value, UserHandle.USER_CURRENT);
            mColorPref.setEnabled(value == 3);
            return true;
        } else if (preference == mColorPref) {
            int accentColor = getAccentColor();
            String hex = ColorPickerPreference.convertToRGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals(String.format("#%06x", (0xFFFFFF & accentColor)))) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex.toUpperCase());
            }
            Settings.System.putStringForUser(resolver,
                    KEY_COLOR, hex.toUpperCase(), UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    private int getAccentColor() {
        final TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
        return value.data;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.settings_edge_lightning);

}
