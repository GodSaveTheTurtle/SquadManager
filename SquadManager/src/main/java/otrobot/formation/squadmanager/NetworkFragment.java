package otrobot.formation.squadmanager;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class NetworkFragment extends Fragment implements NetworkTask.DataSource {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADDR = "defaultAddr";
    private static final String ARG_PORT = "defaultPort";

    private String address;
    private int directionPort;
    private int slavePort;

    private NetworkTask networkTask;

    private int[] axisDelta = new int[2];

    public static NetworkFragment newInstance() {
        NetworkFragment fragment = new NetworkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NetworkFragment() {
        // Required empty public constructor
    }

    private boolean initNetwork(String addr, int port) {
        Log.i(Constants.TAG, "initNetwork");

        try {
            networkTask = new NetworkTask(InetAddress.getByName(addr), port, this);
            return true;
        } catch (UnknownHostException e) {
            Log.wtf(Constants.TAG, e);
            return false;
        }
    }

    private boolean stopNetwork() {
        if (networkTask != null) {
            Log.i(Constants.TAG, "stopNetwork");
            networkTask.cancel(true);
            networkTask = null;
            return true;
        } else {
            Log.i(Constants.TAG, "network already stopped");
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        address = p.getString(getString(R.string.pref_addr_key), getString(R.string.pref_addr_default));
        directionPort = Integer.parseInt(p.getString(getString(R.string.pref_dirPort_key), getString(R.string.pref_dirPort_default)));
        slavePort = Integer.parseInt(p.getString(getString(R.string.pref_slavePort_key), getString(R.string.pref_slavePort_default)));

        initNetwork(address, directionPort);
        if (networkTask != null) networkTask.execute();
    }

    @Override
    public void onPause() {
        stopNetwork();
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_network, container, false);

        // Initialize the network button
        final Button btn = (Button) v.findViewById(R.id.btn);
        updateText(btn);
        btn.setOnClickListener(new NetworkToggleListener(btn));

        final Button btnObstacle = (Button) v.findViewById(R.id.obstacleToggle);
        btnObstacle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });


        ((JoystickView) v.findViewById(R.id.joystickLeft)).setTouchListener(new JoystickView.JoystickTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                axisDelta[0] = y;
            }
        });

        ((JoystickView) v.findViewById(R.id.joystickRight)).setTouchListener(new JoystickView.JoystickTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                axisDelta[1] = x;
            }
        });

        return v;
    }

    private void updateText(Button btn) {
        if (networkTask == null) {
            btn.setText(R.string.connect);
        } else {
            btn.setText(R.string.disconnect);
        }
    }

    @Override
    public String getData() {
        return String.format("d %d %d", axisDelta[0], axisDelta[1]);
    }

    @Override
    public int[] getIntData() {
        return axisDelta;
    }

    private class NetworkToggleListener implements View.OnClickListener {

        private final Button btn;

        private NetworkToggleListener(Button btn) {
            this.btn = btn;
        }

        @Override
        public void onClick(View view) {
            Log.i(Constants.TAG, "Touched");

            if (networkTask == null) {
                if (initNetwork(address, directionPort))
                    networkTask.execute();
            } else {
                stopNetwork();
            }
            updateText(btn);
        }
    }
}
