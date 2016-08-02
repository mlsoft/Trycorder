package net.ddns.mlsoftlaberge.trycorder;

/**
 * Created by mlsoft on 29/07/16.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.trycorder.utils.Fetcher;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TrycorderService extends Service {

    private int NOTIFICATION_ID = 1701;
    private int SERVERPORT = 1701;
    private String deviceName="";

    // the preferences holder
    private SharedPreferences sharedPref;

    private Fetcher mFetcher;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Trycorder Service Starting ...", Toast.LENGTH_LONG).show();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        deviceName = sharedPref.getString("pref_key_device_name", "");

        mFetcher=new Fetcher(getApplicationContext());

        inittalkserver();

        registerService();

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), TrycorderActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Trycorder")
                .setContentText("Talk server running on "+mFetcher.fetch_ip_address()+":1701")
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.trycorder_icon)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterService();
        stoptalkserver();
        stopForeground(true);
        super.onDestroy();
        Toast.makeText(this, "Trycorder Service Destroyed", Toast.LENGTH_LONG).show();
    }

    // =====================================================================================
    // voice receive on udp and playback
    private Thread talkServerThread = null;

    private void inittalkserver() {
        //say("Start talk server thread");
        if(talkServerThread==null) {
            talkServerThread = new Thread(new TalkServerThread());
            talkServerThread.start();
        }
    }

    private void stoptalkserver() {
        //say("Stop the talk server");
        // stop the server thread
        try {
            talkServerThread.interrupt();
        } catch (Exception e) {
            say("cant stop talk server thread");
        }
        talkServerThread=null;
    }

    class TalkServerThread implements Runnable {

        private int RECORDING_RATE = 44100;
        private int CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        private int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

        private int PORT = 1701;

        private int bufferSize = 10000;

        public void run() {

            DatagramSocket talkServerSocket;
            try {
                // create the server socket
                talkServerSocket = new DatagramSocket(null);
                talkServerSocket.setReuseAddress(true);
                talkServerSocket.bind(new InetSocketAddress(PORT));
                Log.d("talkserver", "socket created");
            } catch (Exception e) {
                Log.d("talkserver","talkserverthread Cant create socket");
                return;
            }

            byte[] receiveData = new byte[bufferSize];

            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    RECORDING_RATE, CHANNEL, FORMAT, bufferSize,
                    AudioTrack.MODE_STREAM);
            track.play();

            long systime;
            long lasttime;
            lasttime=0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Log.d("talkloop", "ready to receive " + bufferSize);
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    talkServerSocket.receive(receivePacket);
                    byte[] buffer = receivePacket.getData();
                    int offset = receivePacket.getOffset();
                    int len = receivePacket.getLength();
                    //systime=System.currentTimeMillis();
                    //if((systime-lasttime)>1000) say("Speaking ...");
                    //lasttime=systime;
                    Log.d("talkloop", "received bytes : " + offset + " - " + len);
                    track.write(buffer, offset, len);
                } catch (Exception e) {
                    Log.d("talkloop", "exception: " + e);
                }

            }

            track.flush();
            track.release();
            talkServerSocket.close();

        }

    }

    // =====================================================================================
    // register my service with NSD

    private String mServiceName=null;
    private NsdManager mNsdManager=null;
    private NsdManager.RegistrationListener mRegistrationListener=null;
    private String SERVICE_TYPE = "_http._tcp.";
    private String SERVICE_NAME = "Trycorder";

    public void registerService() {
        if (deviceName.isEmpty()) deviceName = SERVICE_NAME;

        mNsdManager = (NsdManager) getApplicationContext().getSystemService(Context.NSD_SERVICE);

        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(deviceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(SERVERPORT);

        //say("Register service");
        initializeRegistrationListener();

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    public void unregisterService() {
        mNsdManager.unregisterService(mRegistrationListener);
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();
                Log.d("registration", "Registration done: " + mServiceName);
                //saypost("Registration done: " + mServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.d("registration", "Registration failed: " + mServiceName);
                //saypost("Registration failed: " + mServiceName);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d("registration", "Unregistration done");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
                Log.d("registration", "Unregistration failed");
            }
        };
    }

    // ============================================================================
    // pass a message to the user in the appropriate way depending on  ...
    private void say(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
