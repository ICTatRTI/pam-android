package org.rti.rcd.researchstack;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;


public class ApplicationSettingsFragment extends org.researchstack.skin.ui.fragment.SettingsFragment
{
    public static final String KEY_EXAMPLE = "Sample.EXAMPLE";

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.onCreatePreferences(bundle, s);

        // Get our screen which is created in Skin ApplicationSettingsFragment
        PreferenceScreen screen = getPreferenceScreen();

        // Get profile preference
        PreferenceCategory category = (PreferenceCategory) screen.findPreference(KEY_PROFILE);

        // If category exists, we should add mole mapper specific things. If not, that means we
        // are not consented so we have no data to set.
        if(category != null)
        {
            // Example Preference
            Preference checkBoxPref = new Preference(screen.getContext());
            checkBoxPref.setKey(KEY_EXAMPLE);
            checkBoxPref.setTitle("Example Title");
            checkBoxPref.setSummary("You need to extend your settings fragment from Skin's " +
                    "Settings fragment and then modify any preferences that you'd like");
            category.addPreference(checkBoxPref);
        }
    }

    @Override
    public String formatSharingOption(String option) {
        //TODO format when sharing is workin
        return getString(R.string.rsb_consent_share_no);
    }

    @Override
    public String getVersionString()
    {
        return getString(org.researchstack.skin.R.string.rss_settings_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE);
    }

}
