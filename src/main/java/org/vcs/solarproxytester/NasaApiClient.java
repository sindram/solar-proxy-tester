package org.vcs.solarproxytester;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NasaApiClient {

    private static final String API_URL = "https://power.larc.nasa.gov/api/temporal/climatology/point?" +
            "parameters=ALLSKY_SFC_SW_DWN,ALLSKY_SFC_SW_DIFF&" +
            "community=RE&" +
            "longitude=7.922605494683933&" +
            "latitude=48.066150833174866&" +
            "format=JSON";

    public NasaApiClient() {
        setProxyParams();
    }

    public void fetchData() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Request successful!");
            } else {
                System.err.println("Error: HTTP status code " + response.statusCode());
            }
            System.out.println(response.body());
        } catch (java.net.MalformedURLException e) {
            System.err.println("URL not valid: " + e.getMessage());
        } catch (java.net.UnknownHostException e) {
            System.err.println("Network error: could not reach host: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void setProxyParams(){
        // HTTP Proxy Settings
        String httpProxyHost = System.getProperty("http.proxyHost");
        String httpProxyPort = System.getProperty("http.proxyPort");
        String httpProxyUser = System.getProperty("http.proxyUser");
        String httpProxyPassword = System.getProperty("http.proxyPassword");

        if (httpProxyHost != null && httpProxyPort != null) {
            System.setProperty("http.proxyHost", httpProxyHost);
            System.setProperty("http.proxyPort", httpProxyPort);
            System.out.println("HTTP Proxy configured: " + httpProxyHost + ":" + httpProxyPort);
        } else {
            System.out.println("No HTTP Proxy configured.");
        }

        // HTTPS Proxy Settings
        String httpsProxyHost = System.getProperty("https.proxyHost");
        String httpsProxyPort = System.getProperty("https.proxyPort");
        String httpsProxyUser = System.getProperty("https.proxyUser");
        String httpsProxyPassword = System.getProperty("https.proxyPassword");

        if (httpsProxyHost != null && httpsProxyPort != null) {
            System.setProperty("https.proxyHost", httpsProxyHost);
            System.setProperty("https.proxyPort", httpsProxyPort);
            System.out.println("HTTPS Proxy configured: " + httpsProxyHost + ":" + httpsProxyPort);
        } else {
            System.out.println("No HTTPS Proxy configured.");
        }

        // Configure Authenticator for different proxies
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestingProtocol().equalsIgnoreCase("http") &&
                  httpProxyUser != null && httpProxyPassword != null) {
                    System.out.println("Using HTTP Proxy credentials.");
                    return new PasswordAuthentication(httpProxyUser, httpProxyPassword.toCharArray());
                } else if (getRequestingProtocol().equalsIgnoreCase("https") &&
                  httpsProxyUser != null && httpsProxyPassword != null) {
                    System.out.println("Using HTTPS Proxy credentials.");
                    return new PasswordAuthentication(httpsProxyUser, httpsProxyPassword.toCharArray());
                }
                return null;
            }
        });
    }
}
