package org.vcs.solarproxytester;

public class Main {
    public static void main(String[] args) {
        NasaApiClient nac = new NasaApiClient();
        nac.fetchData();
    }
}
