package com.schibsted;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AmplitudeSinkTask extends SinkTask {
    public static final Logger LOG = LoggerFactory.getLogger(AmplitudeSinkTask.class);

    private String amplitude_key;
    private CloseableHttpClient client;

    @Override
    public void start(Map<String, String> props) {
        amplitude_key = props.get("amplitude_key");
        client = HttpClientBuilder.create().build();
    }

    @Override
    public void put(Collection<SinkRecord> records) {

        final Collection<Object> events = records.stream().map(SinkRecord::value).collect(Collectors.toCollection(ArrayList::new));

        if (events.isEmpty()) return;
        LOG.info("events size: {}", events.size());

        HttpPost request = new HttpPost("https://api.amplitude.com/batch");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", this.amplitude_key);
        payload.put("events", events);


        StringEntity entity = null;
        try {
            final String payloadString = objectMapper.writeValueAsString(payload);
            LOG.info(payloadString);

            entity = new StringEntity(payloadString);
            request.setEntity(entity);
            request.setHeader(HttpHeaders.ACCEPT, "*/*");
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            try (CloseableHttpResponse response = this.client.execute(request);) {

                final int statusCode = response.getStatusLine().getStatusCode();
                LOG.info(String.format("\nresponse code: %d", statusCode));
                switch (statusCode) {
                    case HttpStatus.SC_OK:
                        LOG.debug("amplitude batch upload OK");
                        break;
                    default:
                        LOG.info("Batch Upload to amplitude failed with status code {}", statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        LOG.info("sink task stop");
    }

    @Override
    public String version() {
        return "1.0";
    }
}
