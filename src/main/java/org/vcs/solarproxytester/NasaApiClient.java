package org.vcs.solarproxytester;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NasaApiClient {

    private static final String API_URL = "https://power.larc.nasa.gov/api/temporal/climatology/point?parameters=ALLSKY_SFC_SW_DWN,ALLSKY_SFC_SW_DIFF&community=RE&longitude=7.922605494683933&latitude=48.066150833174866&format=JSON";

    public NasaApiClient() {}

    public void fetchData() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Anfrage erfolgreich! Verarbeite Daten...");
            } else {
                System.err.println("Fehler: HTTP-Statuscode " + response.statusCode());
            }
            System.out.println(response.body());
        } catch (java.net.MalformedURLException e) {
            System.err.println("Ungültige URL: " + e.getMessage());
        } catch (java.net.UnknownHostException e) {
            System.err.println("Netzwerkfehler: Host konnte nicht erreicht werden: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ein Fehler ist aufgetreten: " + e.getMessage());
        }
    }


}
