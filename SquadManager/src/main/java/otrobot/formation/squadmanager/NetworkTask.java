package otrobot.formation.squadmanager;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nicolas on 2/21/14.
 */
public class NetworkTask extends AsyncTask<Void, Void, Void>{


    private DatagramSocket socket;
    private InetAddress url;
    private int port;
    private BlockingQueue<String> dataQueue;
    boolean running;
    public static final String MSG_TERMINATE = "TERMINATE";

    public NetworkTask(InetAddress url, int port, BlockingQueue<String> data) {
        System.out.println("Creating network task");
        this.url = url;
        this.port = port;
        this.dataQueue = data;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        running = true;
        try {
            socket = new DatagramSocket();

            while(running) {
                String data = null;
                Log.i(Constants.TAG, "Waiting for dataqueue");
                data = dataQueue.take();
                Log.i(Constants.TAG, "got something!");
                if (MSG_TERMINATE.equals(data)) running = false;
                else {
                    Log.d(Constants.TAG, "Sending: " + data);
                    byte[] buffer = data.getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, url, port));
                }
            }

            socket.close();

        } catch (IOException e) {
            Log.wtf(Constants.TAG, e);
        } catch (InterruptedException e) {
            Log.w(Constants.TAG, e);
        }

        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        dataQueue.add(MSG_TERMINATE);
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
}
