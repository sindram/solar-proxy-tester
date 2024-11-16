# Solar Proxy Tester
In many cases https requests from within a company network are blocked by a proxy server. 
This project provides a Java application that tests if a proxy server is blocking https 
requests called from within a JAVA tool. The main use case is to test the solar task of VC Publisher. 
For this reason a https call is made to the public API hosted by [NASA Prediction Of Worldwide Energy Resources](https://power.larc.nasa.gov)

The tool should help to properly set up VC Publisher and avoid Proxy issues.

## Basic Usage
1. **Run the Docker container**:
```sh
    docker run -it --name solarproxytester ghcr.io/sindram/solar-proxy-tester:1.0.0
```
2. **Open bash in Docker container**:
```sh
    docker exec -it solarproxytester /bin/bash
```
3. **Change to working directory**:
```sh
   cd /solar-proxy-tester/lib  
```
4. **Run Solar Proxy Tester**
```sh
   java -jar solar-proxy-tester-1.0.0.jar
```

   Solar Proxy Tester is calling
   ```
   https://power.larc.nasa.gov/api/temporal/climatology/point?parameters=ALLSKY_SFC_SW_DWN,ALLSKY_SFC_SW_DIFF&community=RE&longitude=7.922605494683933&latitude=48.066150833174866&format=JSON
   ```
   and should return this JSON response
   ```json
   {"type":"Feature","geometry":{"type":"Point","coordinates":[7.922605494683933,48.066150833174866,702.23]},"properties":{"parameter":{"ALLSKY_SFC_SW_DWN":{"JAN":1.0,"FEB":1.76,"MAR":3.04,"APR":4.46,"MAY":5.21,"JUN":5.87,"JUL":5.67,"AUG":4.83,"SEP":3.72,"OCT":2.2,"NOV":1.15,"DEC":0.81,"ANN":3.32},"ALLSKY_SFC_SW_DIFF":{"JAN":0.59,"FEB":0.94,"MAR":1.49,"APR":2.1,"MAY":2.58,"JUN":2.88,"JUL":2.64,"AUG":2.26,"SEP":1.66,"OCT":1.16,"NOV":0.68,"DEC":0.48,"ANN":1.62}}},"header":{"title":"NASA/POWER CERES/MERRA2 Native Resolution Climatology Climatologies","api":{"version":"v2.5.9","name":"POWER Climatology API"},"sources":["syn1deg","ceres"],"fill_value":-999.0,"range":"20-year Meteorological and Solar Monthly & Annual Climatologies (January 2001 - December 2020)"},"messages":[],"parameters":{"ALLSKY_SFC_SW_DWN":{"units":"kW-hr/m^2/day","longname":"All Sky Surface Shortwave Downward Irradiance"},"ALLSKY_SFC_SW_DIFF":{"units":"kW-hr/m^2/day","longname":"All Sky Surface Shortwave Diffuse Irradiance"}},"times":{"data":1.671,"process":0.13}}
   ```

## Define Proxy/Java Params
In Java, when you want to specify proxy settings like the proxy host, port, and optionally, 
the authentication details. The `-D` option is used to set Java system properties at runtime.

With the help of the ```JAVA_TOOL_OPTIONS``` env you are able to define your desired set of options.

```sh
   exort JAVA_TOOL_OPTIONS=<define your parameters>
```

### Java Proxy Parameters Table

| **Parameter**         | **Description**                                              | **Example**                           |
|-----------------------|--------------------------------------------------------------|---------------------------------------|
| `http.proxyHost`      | The hostname or IP address of the HTTP proxy server.         | `-Dhttp.proxyHost=proxy.example.com`  |
| `http.proxyPort`      | The port number for the HTTP proxy server.                   | `-Dhttp.proxyPort=8080`               |
| `http.proxyUser`      | The username for authentication with the HTTP proxy.         | `-Dhttp.proxyUser=username`           |
| `http.proxyPassword`  | The password for authentication with the HTTP proxy.         | `-Dhttp.proxyPassword=password`       |
| `https.proxyHost`     | The hostname or IP address of the HTTPS proxy server.        | `-Dhttps.proxyHost=proxy.example.com` |
| `https.proxyPort`     | The port number for the HTTPS proxy server.                  | `-Dhttps.proxyPort=8080`              |
| `https.proxyUser`     | The username for authentication with the HTTP proxy.         | `-Dhttps.proxyUser=username`          |
| `https.proxyPassword` | The password for authentication with the HTTP proxy.         | `-Dhttps.proxyPassword=password`      |

### Example of a Full Command with Proxy Parameters:

```bash
   export JAVA_TOOL_OPTIONS="\
  -Dhttp.proxyHost=proxy.example.com \
  -Dhttp.proxyPort=8080 \
  -Dhttps.proxyHost=proxy.example.com \
  -Dhttps.proxyPort=8080 \
  -Dhttp.proxyUser=username \
  -Dhttp.proxyPassword=password"
```

### Further Parameters (not complete)
In some cases it might be necessary to add further parameters to simplify e.g. debugging. In the following there is
an incomplete list. For further edge cases we refer to documentations from common experts.

| **Parameter**     | **Description**                                   | **Example**                               |
|-------------------|---------------------------------------------------|-------------------------------------------|
| `javax.net.debug` | Log detailed information related to SSL/TLS       | `-Djavax.net.debug=ssl:handshake`         |
| `javax.net.debug` | Logs all SSL/TLS events                           | `-Djavax.net.debug=all`                   |
| `javax.net.debug` | Logs handshake events with more verbose output    | `-Djavax.net.debug=ssl:handshake:verbose` |

Java is utilizing a default `KeyStore` and `TrustStore`. In some rare cases there could be different ones.

| **Parameter**     | **Description**      | **Example**                                        |
|-------------------|----------------------|----------------------------------------------------|
| `javax.net.ssl`   | Path to TrustStore   | `-Djavax.net.ssl.trustStore=/path/to/myTrustStore` |
| `javax.net.ssl`   | TrustStore password  | `-Djavax.net.ssl.trustStorePassword=changeit`      |
| `javax.net.ssl`   | Path to KeyStore     | `-Djavax.net.ssl.keyStore=/path/to/myKeyStore`     |
| `javax.net.ssl`   | KeyStore password    | `-Djavax.net.ssl.keyStorePassword=changeit`        |

### Certificates
When running Java applications behind a proxy, especially in secure environments where the proxy intercepts and 
re-encrypts HTTPS traffic, you may need to configure Java to trust the proxy's SSL/TLS 
certificates. This is done by importing the proxy's SSL certificate into Java's trust store.

The proxy server typically has its own SSL certificate, which you need to import into your Java trust store. 
You can export the proxy certificate using your browser or using command-line tools like `openssl`.

Example using openssl to extract the certificate from a proxy server:
```bash
   openssl s_client -connect <proxy_host>:<proxy_port> -showcerts
```
You’ll see the proxy server's certificate in the output. Copy the certificate block (from `-----BEGIN CERTIFICATE-----` 
to `-----END CERTIFICATE----- `) into a `.crt` file.

In order to import the certificate to the TrustStore of JAVA within the container 
your can utilize Docker `cp` from your host machine.
```bash
   docker cp /path/to/my.crt solarproxytester:/usr/local/share/ca-certificates/
```
Finally, you have to update certificate store. 
```bash
   docker exec -u root solarproxytester update-ca-certificates
```
You are able to verify the registration of your certificate on the container `bash`
```bash
   keytool -list -keystore /etc/ssl/certs/java/cacerts -storepass changeit
```
The same is valid for other certificated e.g. ca-root certificates.

## Setup
If you want to adapt the code to your own needs, you need to build the tool. It is recommended to use official versions.

### Prerequisites
- Java 11
- Gradle
- Docker
### Building
1. **Clone the repository**:
    ```sh
    git clone https://github.com/sindram/solar-proxy-tester.git
    cd solar-proxy-tester
    ```
2. **Build the project**:
    ```sh
    ./gradlew build
    ```
3. **Create the Docker image**:
    ```sh
    ./gradlew buildDockerImage
    ```

## License
This project is licensed under the MIT License.
