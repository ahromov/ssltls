import java.io.*;
import java.net.InetSocketAddress;
import java.lang.*;

import com.sun.net.httpserver.HttpsServer;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.*;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import javax.net.ssl.SSLContext;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SSLServer {

    private static Integer SERVER_PORT = 8000;

    public static void main(String[] args) {
        try {
            // setup the socket address
            InetSocketAddress address = new InetSocketAddress(SERVER_PORT);

            // initialize the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "example".toCharArray();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            final InputStream keystore = SSLServer.class.getClassLoader().getResourceAsStream("keystore.p12");
            ks.load(keystore, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Set the SSL parameters
                        SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);
                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                    }
                }
            });
            httpsServer.createContext("/", new MyHandler());
            httpsServer.setExecutor(null); // creates a default executor
            httpsServer.start();
        } catch (Exception exception) {
            System.out.println("Failed to create HTTPS server on port " + SERVER_PORT);
            exception.printStackTrace();
        }
    }

    public static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Server: This is the servers response";
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }
}