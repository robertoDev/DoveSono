package it.blackcat.dovesono;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import it.blackcat.R;

/**
 * Created with IntelliJ IDEA.
 * User: homeuser
 * Date: 16/12/12
 * Time: 16.23
 * To change this template use File | Settings | File Templates.
 */
public class ShowPrefsActivity extends PreferenceActivity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}