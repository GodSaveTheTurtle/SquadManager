package otrobot.formation.squadmanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class NetworkFragment extends Fragment implements NetworkTask.DataSource{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADDR = "addr";
    private static final String ARG_PORT = "port";

    private String addr;
    private int port;

    private NetworkTask networkTask;

    private int[] axisDelta = new int[2];

    private Button btn;

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

    private void initNetwork(String addr, int port) {
        Log.i(Constants.TAG, "initNetwork");

        try {
            networkTask = new NetworkTask(InetAddress.getByName(addr), port, this);
        } catch (UnknownHostException e) {
            Log.wtf(Constants.TAG, e);
        }
    }

    private void stopNetwork() {
        if (networkTask != null) {
            Log.d(Constants.TAG, "stopNetwork");
            networkTask.cancel(true);
            networkTask = null;
        } else {
            Log.d(Constants.TAG, "network already stopped");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "NF creation");

        if (getArguments() != null) {
            addr = getArguments().getString(ARG_ADDR);
            port = getArguments().getInt(ARG_PORT);
            initNetwork(addr, port);
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
        btn = (Button)v.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.TAG, "Touched");
                if (addr == null) Log.w(Constants.TAG, "No server address provided");
                else if (networkTask == null) {
                    initNetwork(addr, port);
                    networkTask.execute();
                } else {
                    stopNetwork();
                }
            }
        });
        ((JoystickView)v.findViewById(R.id.joystickLeft)).setTouchListener(new JoystickView.JoystickTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                Log.i(Constants.TAG, "Vertical: " + y);
                axisDelta[1] = y;
            }
        });

        ((JoystickView)v.findViewById(R.id.joystickRight)).setTouchListener(new JoystickView.JoystickTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                Log.i(Constants.TAG, "Horizontal: " + x);
                axisDelta[0] = x;
            }
        });
        return v;
    }

    @Override
    public String getData() {
        return String.format("%d %d", axisDelta[0], axisDelta[1]);
    }

    @Override
    public int[] getIntData() {
        return axisDelta;
    }
}
