package ro.pub.cs.systems.eim.practicaltest02v3.network;

import android.util.Log;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02v3.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v3.general.Utilities;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String name;
    private final TextView resultView;

    private Socket socket;

    public ClientThread(String address, int port, String name, TextView resultView) {
        this.address = address;
        this.port = port;
        this.name = name;
        this.resultView = resultView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[CLIENT THREAD] Sending name: " + name);
            printWriter.println(name);
            printWriter.flush();
            String dictionaryInformation;
            dictionaryInformation = bufferedReader.readLine();
            final String finalizedDictionaryInformation = dictionaryInformation;
            resultView.post(() -> {
                resultView.setText(finalizedDictionaryInformation);
            });
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
