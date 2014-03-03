package otrobot.formation.squadmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class NetworkFragment extends Fragment implements NetworkTask.DataSource {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADDR = "defaultAddr";
    private static final String ARG_PORT = "defaultPort";

    private String defaultAddr;
    private int defaultPort;

    private NetworkTask networkTask;

    private int[] axisDelta = new int[2];

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param addr Parameter 1.
     * @param port Parameter 2.
     * @return A new instance of fragment NetworkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NetworkFragment newInstance(String addr, int port) {
        NetworkFragment fragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ADDR, addr);
        args.putInt(ARG_PORT, port);
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
        Log.i(Constants.TAG, "NF creation");

        if (getArguments() != null) {
            defaultAddr = getArguments().getString(ARG_ADDR);
            defaultPort = getArguments().getInt(ARG_PORT);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
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

        // Set the default address
        final EditText addressField = (EditText) v.findViewById(R.id.editIP);
        addressField.setText(String.format("%s:%d", defaultAddr, defaultPort));

        // Initialize the button
        final Button btn = (Button) v.findViewById(R.id.btn);
        updateText(btn);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(Constants.TAG, "Touched");

                String addr;
                int port;

                if (addressField.getText().toString() != null) {
                    String[] tmp = addressField.getText().toString().split(":");
                    addr = tmp[0];
                    port = tmp.length > 1 ? Integer.getInteger(tmp[1], defaultPort) : defaultPort;
                } else {
                    addr = defaultAddr;
                    port = defaultPort;
                }
                if (networkTask == null) {
                    if (initNetwork(addr, port))
                        networkTask.execute();
                } else {
                    stopNetwork();
                }
                updateText(btn);
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
}
