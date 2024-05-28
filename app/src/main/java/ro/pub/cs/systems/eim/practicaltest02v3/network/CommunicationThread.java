package ro.pub.cs.systems.eim.practicaltest02v3.network;

import android.util.Log;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02v3.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v3.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02v3.model.DictionaryInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommunicationThread extends Thread {
    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (pokemon name)!");
            String name = bufferedReader.readLine();
            if (name == null || name.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (name)!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
            String url = Constants.WEB_SERVICE_ADDRESS +  name;
            URL urlAddress = new URL(url);
            URLConnection urlConnection = urlAddress.openConnection();
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String pageSourceCode;
            StringBuilder stringBuilder = new StringBuilder();
            String currentLine;
            while ((currentLine = bufferedReader1.readLine()) != null) {
                stringBuilder.append(currentLine);
            }
            bufferedReader1.close();
            pageSourceCode = stringBuilder.toString();


            JSONArray jsonArray = new JSONArray(pageSourceCode);
            JSONObject firstObject = jsonArray.getJSONObject(0);

            // Get the 'meanings' array
            JSONArray meaningsArray = firstObject.getJSONArray("meanings");

            // Get the first meaning object
            JSONObject firstMeaning = meaningsArray.getJSONObject(0);

            // Get the 'definitions' array from the first meaning
            JSONArray definitionsArray = firstMeaning.getJSONArray("definitions");

            // Get the first definition object
            JSONObject firstDefinition = definitionsArray.getJSONObject(0);

            // Extract the 'definition' field
            String definition = firstDefinition.getString("definition");
            DictionaryInformation defInfo = new DictionaryInformation(definition);
            Log.d(Constants.TAG, defInfo.toString());

            printWriter.println(defInfo.getDefinitions());
            printWriter.flush();
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}