package org.example;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;

public class SSLClient {

//    private static String HOSTNAME = "google.com";
//    private static Integer PORT = 443;

    private static String HOSTNAME = "localhost";
    private static Integer PORT = 8443;

    private static String KEY_STORE_PASSWORD = "Gromov85";

    public static void main(String[] args) throws IOException {
//        SocketFactory sslsocketfactory = SSLSocketFactory.getDefault();
        SocketFactory sslsocketfactory = getSocketFactory();
        SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(HOSTNAME, PORT);

        InputStream in = sslsocket.getInputStream();
        OutputStream out = sslsocket.getOutputStream();

        out.write(1);
        while (in.available() > 0) {
            System.out.print(in.read());
        }

        System.out.println("Secured connection performed successfully");
    }

    public static SSLSocketFactory getSocketFactory() {
        char[] password = KEY_STORE_PASSWORD.toCharArray();

        try {
            final KeyStore ks = KeyStore.getInstance("PKCS12");
            final InputStream keystore = SSLClient.class.getClassLoader().getResourceAsStream("truststore/sslserver.p12");
            if (keystore == null) {
                throw new RuntimeException("Keystore required!");
            }
            ks.load(keystore, password);

            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(ks, password);

            final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
