package net.ddns.mlsoftlaberge.trycorder;

/**
 * Created by mlsoft on 29/07/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
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
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.trycorder.tryclient.TryclientActivity;
import net.ddns.mlsoftlaberge.trycorder.utils.Fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrycorderService extends Service {

    private int NOTIFICATION_ID = 1701;
    private int SERVERPORT = 1701;
    public final static String BROADCAST_ACTION="net.ddns.mlsoftlaberge.trycorder.updateservice";

    private Intent intentForActivity;

    // the preferences holder
    private SharedPreferences sharedPref;

    private Fetcher mFetcher;

    private Handler mHandler;

    private IBinder mBinder=new TryBinder();

    public class TryBinder extends Binder {

        public TrycorderService getService() {
            return(TrycorderService.this);
        }

        public List<String> getiplist() {
            return(mIpList);
        }

        public List<String> getnamelist() {
            return(mNameList);
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    private String deviceName="";
    private boolean autoListen;
    private String speakLanguage;
    private String listenLanguage;
    private boolean autoBoot;
    private boolean autoStop;

    @Override
    public void onCreate() {
        super.onCreate();
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Trycorder Service Created ...", Toast.LENGTH_LONG).show();

        mFetcher=new Fetcher(getApplicationContext());

        mHandler=new Handler();

        intentForActivity = new Intent(BROADCAST_ACTION);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        autoListen = sharedPref.getBoolean("pref_key_auto_listen", false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");
        deviceName = sharedPref.getString("pref_key_device_name", "Trycorder");
        autoBoot = sharedPref.getBoolean("pref_key_auto_boot", true);
        autoStop = sharedPref.getBoolean("pref_key_auto_stop", false);
        if(deviceName.equals("Trycorder")) {
            deviceName=mFetcher.fetch_device_name();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("pref_key_device_name", deviceName);
            editor.commit();
        }

        initserver();

        inittalkserver();

        registerService();

        startdiscoverService();

    }

    private Notification mNotification=null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mNotification==null) {
            mNotification = getNotification("Starting...");
            startForeground(NOTIFICATION_ID, mNotification);
            Toast.makeText(this, "Trycorder Service Started.", Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopdiscoverService();
        unregisterService();
        stoptalkserver();
        stopserver();
        stopForeground(true);
        super.onDestroy();
        Toast.makeText(this, "Trycorder Service Destroyed", Toast.LENGTH_LONG).show();
    }

    // ============================================================================

    // prepare a notification with the text
    private Notification getNotification(String text){
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), TryclientActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Trycorder " + deviceName)
                .setContentText("Talk Server running on "+mFetcher.fetch_ip_address()+":"+SERVERPORT)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.trycorder_icon)
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .build();

        return(notification);
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification(String text) {

        Notification notification = getNotification(text);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    // =========================================================================
    // usage of text-to-speech to speak a sensence
    // =========================================================================
    // usage of text-to-speech to speak a sensence
    private TextToSpeech tts=null;

    private void initspeak() {
        if(tts==null) {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        setspeaklang(speakLanguage);
                    }
                }
            });
        }
    }

    public void setspeaklang(String lng) {
        if (lng.equals("FR")) {
            tts.setLanguage(Locale.FRENCH);
        } else if (lng.equals("EN")) {
            tts.setLanguage(Locale.US);
        } else {
            // default prechoosen language
        }
    }

    public void speak(String texte) {
        initspeak();
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
        say("Speaked: "+texte);
    }

    public void speak(String texte,String lng) {
        initspeak();
        setspeaklang(lng);
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
        say("Speaked: "+texte);
    }

    // =====================================================================================
    // network operations.   ===   Hi Elvis!
    // =====================================================================================

    private ServerSocket serverSocket = null;

    private Thread serverThread = null;

    // initialize the servers
    private void initserver() {
        say("Initialize the network server");
        // create the handler to receive events from communication thread
        //mHandler = new Handler();
        // start the server thread
        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }

    // stop the servers
    private void stopserver() {
        say("Stop the network");
        // stop the server thread
        serverThread.interrupt();
        // close the socket of the server
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ====================================================================================
    // server part

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    Log.d("serverthread", "accepting server socket");
                    saypost("Accepting server socket");
                    socket = serverSocket.accept();
                    Log.d("serverthread", "accepted server socket");
                    saypost("Accepted socket");

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    Log.d("serverthread", "exception " + e.toString());
                    saypost("Exception: "+e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket commSocket;

        private BufferedReader bufinput;

        public CommunicationThread(Socket socket) {

            commSocket = socket;

            try {

                bufinput = new BufferedReader(new InputStreamReader(commSocket.getInputStream()));

            } catch (IOException e) {
                Log.d("commthreadinit", "exception " + e.toString());
                e.printStackTrace();
            }
        }

        public void run() {
            saypost("Receiving text");
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = bufinput.readLine();
                    if (read == null) break;
                    Log.d("commthreadrun", "update conversation");
                    mHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    Log.d("commthreadrun", "exception " + e.toString());
                    e.printStackTrace();
                }
            }
        }

    }

    // tread to update the ui
    class updateUIThread implements Runnable {
        private String msg = null;

        public updateUIThread(String str) {
            msg = str;
            Log.d("uithread", str);
        }

        @Override
        public void run() {
            if (msg != null) {
                displaytext(msg);
            }
        }
    }

    public void displaytext(String msg) {
        informActivity("text",msg);
    }


    // =====================================================================================
    // voice receive on udp and playback
    private Thread talkServerThread = null;

    private void inittalkserver() {
        say("Start talk server thread");
        if(talkServerThread==null) {
            talkServerThread = new Thread(new TalkServerThread());
            talkServerThread.start();
        }
    }

    private void stoptalkserver() {
        say("Stop the talk server");
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

        say("Register service");
        initializeRegistrationListener();

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    public void unregisterService() {
        try {
            mNsdManager.unregisterService(mRegistrationListener);
        } catch (Exception e) {
            Log.d("unregisterservice","Error "+e);
        }
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
                saypost("Registration done: " + mServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.d("registration", "Registration failed: " + mServiceName);
                saypost("Registration failed: " + mServiceName);
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



    // =======================================================================================
    // discovery section

    private NsdManager.DiscoveryListener mDiscoveryListener=null;
    private NsdManager.ResolveListener mResolveListener=null;

    private List<String> mIpList = new ArrayList<String>();
    private List<String> mNameList = new ArrayList<String>();

    public void startdiscoverService() {
        if (deviceName.isEmpty()) deviceName = SERVICE_NAME;

        mIpList.clear();
        mIpList.add(mFetcher.fetch_ip_address());
        mNameList.clear();
        mNameList.add(deviceName);

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        say("Discover services");
        initializeResolveListener();

        initializeDiscoveryListener();

        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

    }

    public void stopdiscoverService() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                //Log.e("discovery", "Resolve failed: " + errorCode);
                //saypost("Resolve failed: " + serviceInfo.getServiceName() + " Err:" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                //Log.e("discovery", "Resolve Succeeded: " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    //Log.d("discovery", "Same IP.");
                    //saypost("Local machine " + mServiceName);
                    //return;
                }
                int port = serviceInfo.getPort();
                InetAddress host = serviceInfo.getHost();
                //Log.d("discovery", "Host: " + host.toString() + " Port: " + port);
                //saypost("Resolved " + serviceInfo.getServiceName() +
                //        " Host: " + host.toString() + " Port: " + port);
                StringBuffer str = new StringBuffer(host.toString());
                addiplist(str.substring(1), serviceInfo.getServiceName());
            }
        };
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                //Log.d("discovery", "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                //Log.d("discovery", "Service discovery success: " + service);
                //saypost("Service discovered: " + service.getServiceName());
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    //Log.d("discovery", "Unknown Service Type: " + service.getServiceType());
                } else {
                    if (service.getServiceName().equals(mServiceName)) {
                        // The name of the service tells the user what they'd be
                        // connecting to. It could be "Bob's Chat App".
                        //Log.d("discovery", "Same machine: " + mServiceName);
                        return;
                    }
                    //Log.d("discovery", "Resolved service: " + service.getServiceName());
                    //Log.d("discovery", "Resolved service: " + service.getHost());  // empty
                    //Log.d("discovery", "Resolved service: " + service.getPort());  // empty
                    try {
                        mNsdManager.resolveService(service, mResolveListener);
                    } catch (Exception e) {
                        //Log.d("discovery", "resolve error: " + e.toString());
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                //Log.e("discovery", "service lost: " + service);
                //saypost("Lost: " + service.getServiceName());
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                //Log.i("discovery", "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                //Log.e("discovery", "Start Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                //Log.e("discovery", "Stop Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private void addiplist(String ip, String name) {
        for (int i = 0; i < mIpList.size(); ++i) {
            if (ip.equals(mIpList.get(i))) {
                listpost();
                return;
            }
        }
        // replace the \032 on android 4.4.4 by a blank space
        String newname = name.replaceFirst("032"," ").replace('\\',' ');
        mIpList.add(ip);
        mNameList.add(newname);
        listpost();
        saypost("Added "+ip+" - "+newname);
        informActivity("iplist",ip);
        Toast.makeText(this, "New IP found: "+ip, Toast.LENGTH_LONG).show();
    }

    public void listpost() {
        mHandler.post(new listThread());
    }

    // tread to update the ui
    class listThread implements Runnable {

        public listThread() {
        }

        @Override
        public void run() {
            saylist();
        }
    }

    public void saylist() {
        StringBuffer str = new StringBuffer("");
        for (int i = 0; i < mIpList.size(); ++i) {
            str.append(mIpList.get(i) + " - " + mNameList.get(i) + "\n");
        }
        updateNotification(str.toString());
    }

    // post something to say on the main thread (from a secondary thread)
    public void saypost(String str) {
        mHandler.post(new sayThread(str));
    }

    // tread to update the ui
    class sayThread implements Runnable {
        private String msg;

        public sayThread(String str) {
            msg = str;
            //Log.d("saythread", str);
        }

        @Override
        public void run() {
            if (msg != null) {
                say(msg);
            }
        }
    }

    // pass a message to the user in the appropriate way depending on  ...
    private void say(String msg) {
        informActivity("say",msg);
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private void informActivity(String cmd, String text) {
        intentForActivity.putExtra("TRYSERVERCMD", cmd);
        intentForActivity.putExtra("TRYSERVERTEXT", text);
        sendBroadcast(intentForActivity);
    }


}
