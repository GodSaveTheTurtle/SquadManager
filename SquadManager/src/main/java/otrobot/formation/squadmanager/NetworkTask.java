package otrobot.formation.squadmanager;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created by nicolas on 2/21/14.
 */
public class NetworkTask extends AsyncTask<Void, Void, Void> {


    private DatagramSocket socket;
    private InetAddress url;
    private int port;
    boolean running;
    private DataSource dataSource;
    private long SEND_INTERVAL_MILLIS = 100;

    public NetworkTask(InetAddress url, int port, DataSource dataSource) {
        Log.i(Constants.TAG, "Creating network task");
        this.url = url;
        this.port = port;
        this.dataSource = dataSource;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        running = true;
        try {
            socket = new DatagramSocket();

            while (running) {
                /*int[] data = dataSource.getIntData();
                Log.d(Constants.TAG, "Sending: " + Arrays.toString(data));
                byte[] buffer = intsToBytes(data);*/

                String data = dataSource.getData();
                Log.d(Constants.TAG, "Sending: " + data);
                byte[] buffer = data.getBytes();

                socket.send(new DatagramPacket(buffer, buffer.length, url, port));
                // todo flush socket?
                Thread.sleep(SEND_INTERVAL_MILLIS);
            }

            socket.close();

        } catch (IOException e) {
            Log.wtf(Constants.TAG, e);
        } catch (InterruptedException e) {
            Log.w(Constants.TAG, "Network Task interrupted");
        }

        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        running = false;
        if (socket != null) {
            socket.close();
            socket = null;
        }
        super.onCancelled(aVoid);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO: update the Kinect view here?
        super.onProgressUpdate(values);
    }

    public static byte[] intsToBytes(int[] ints) {
        ByteBuffer bb = ByteBuffer.allocate(ints.length * 4);
        IntBuffer fb = bb.asIntBuffer();
        for (int f : ints) fb.put(f);
        return bb.array();
    }

    /**
     * Used to retrieve the data to send on the network
     */
    public static interface DataSource {
        String getData();

        int[] getIntData();
    }
}
