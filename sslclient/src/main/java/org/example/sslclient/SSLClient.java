package org.example.sslclient;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;


public class SSLClient {

    private static String HOSTNAME = "localhost";
    private static Integer PORT = 8000;

    private static String KEY_STORE_PASSWORD = "example";

    public static void main(String[] args) throws IOException {
//        SocketFactory sslsocketfactory = getSocketFactory();
        SocketFactory sslsocketfactory = SSLSocketFactory.getDefault();
        SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(HOSTNAME, PORT);
        sslsocket.startHandshake();
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream())));
        sendRequest(out);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
        printData(inputReader);
        close(sslsocket, out, inputReader);
        System.out.println("Client: Secured connection performed successfully");
    }

    private static void sendRequest(PrintWriter out) {
        out.println("GET / HTTP/1.0");
        out.println();
        out.flush();
    }

    private static void printData(BufferedReader inputReader) throws IOException {
        String inputLine;
        while ((inputLine = inputReader.readLine()) != null)
            System.out.println(inputLine);
    }

    public static SSLSocketFactory getSocketFactory() {
        char[] password = KEY_STORE_PASSWORD.toCharArray();
        try {
            final KeyStore keyStore = KeyStore.getInstance("PKCS12");
            final InputStream keystore = SSLClient.class.getClassLoader().getResourceAsStream("keystore.p12");
            if (keystore == null) {
                throw new RuntimeException("Keystore required!");
            }
            keyStore.load(keystore, password);
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, password);
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void close(SSLSocket sslsocket, PrintWriter out, BufferedReader in) throws IOException {
        in.close();
        out.close();
        sslsocket.close();
    }
}
