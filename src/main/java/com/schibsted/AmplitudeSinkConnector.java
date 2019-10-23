package com.schibsted;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmplitudeSinkConnector extends SinkConnector {
    public static final Logger LOG = LoggerFactory.getLogger(AmplitudeSinkConnector.class);
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> props) {
        configProps = props;

        LOG.info("sink connector start");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return AmplitudeSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        LOG.info("sink connector taskConfigs");
        final ArrayList toReturn = new ArrayList();
        for (int i = 0; i < maxTasks; i++) {
            toReturn.add(configProps);
        }
        return toReturn;
    }

    @Override
    public void stop() {
        LOG.info("sink connector stop");
    }

    @Override
    public ConfigDef config() {
        LOG.info("sink connector config");
        ConfigDef CONFIG_DEF = new ConfigDef()
                .define("amplitude_key", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "The amplitude project key")
                ;
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return "1.0";
    }
}
