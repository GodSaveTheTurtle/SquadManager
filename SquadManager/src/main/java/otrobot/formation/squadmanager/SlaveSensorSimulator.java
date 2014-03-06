package otrobot.formation.squadmanager;

import android.util.Log;

import java.util.concurrent.BlockingQueue;

public class SlaveSensorSimulator implements Runnable {

    private final BlockingQueue<String> dataSource;
    private boolean running = false;

    public SlaveSensorSimulator(BlockingQueue<String> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run() {
        running = true;

        // TODO Socket create

        while (running) {
            try {
                String data = dataSource.take();

                // TODO: Socket send

            } catch (InterruptedException e) {
                Log.w(Constants.TAG, e);
                break;
            }
        }
    }
}
