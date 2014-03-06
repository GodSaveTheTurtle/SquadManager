package otrobot.formation.squadmanager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    /**
     * As attribute to avoid recreating when coming back from the settings
     */
    private NetworkFragment networkFragment;

    private String address;
    private int directionPort;
    private int slavePort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
        if (savedInstanceState == null) {
            displayJoysticks();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menu.findItem(R.id.menu_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                displaySettings();
                return true;
            }
        });

        menu.findItem(R.id.menu_toggleSlaveSimulator).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // TODO toggle sensor simulator, set attribute
                Log.i(Constants.TAG, "TODO Toggle slave simulator");
                return true;
            }
        });

        return true;
    }

    private void displaySettings() {
        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void displayJoysticks() {
        if (networkFragment == null) networkFragment = NetworkFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.root_layout, networkFragment).commit();
    }

    public String getAddress() {
        return address;
    }

    public int getDirectionPort() {
        return directionPort;
    }

    public int getSlavePort() {
        return slavePort;
    }
}
