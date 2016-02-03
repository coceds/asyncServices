package facade.config;

import com.google.common.base.Throwables;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 10;

    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

    @Bean(destroyMethod = "destroy")
    public AsyncClientHttpRequestFactory asyncHttpRequestFactory() {
        return new HttpComponentsAsyncClientHttpRequestFactory(
                asyncHttpClient());
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        AsyncRestTemplate restTemplate = new AsyncRestTemplate(
                asyncHttpRequestFactory(), new RestTemplate());
        return restTemplate;
    }


    @Bean(destroyMethod = "close", initMethod = "start")
    public CloseableHttpAsyncClient asyncHttpClient() {
        try {
            PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
                    new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
            connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
            connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                    .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                    .setConnectionRequestTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                    .build();

            CloseableHttpAsyncClient httpclient = HttpAsyncClientBuilder
                    .create().setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(config).build();
            httpclient.start();
            return httpclient;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}

