/**
 * Created by jyot on 23/6/17.
 */

import com.google.gson.Gson;
import models.Config;
import models.Metric;
import models.Node;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CassandraMonitor {

    public static void main(String args[]) throws Exception {

        Config config = loadConfig();

        for (Node node : config.getNodes()) {
            String url = "service:jmx:rmi:///jndi/rmi://" + node.getHost() + ":" + node.getPort() + "/jmxrmi";

            JMXServiceURL serviceUrl = new JMXServiceURL(url);
            try {
                JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
                MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
                validate(config, mBeanServerConnection, node);
                jmxConnector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Config loadConfig() throws FileNotFoundException {
        String path = System.getenv("CONFIG");
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String jsonString = String.join("\n", reader.lines().collect(Collectors.toList())).replaceAll("\n", "").replaceAll(" ", "");
        return new Gson().fromJson(jsonString, Config.class);
    }

    private static void validate(Config config, MBeanServerConnection mBeanServerConnection, Node node) throws Exception {
        Validator validator = new Validator(config, node);

        validator.checkHosts((List) getMBeanAttribute(mBeanServerConnection, new Metric("org.apache.cassandra.db:type=StorageService", "LiveNodes")));

        for (Metric metric : config.getMetrics()) {
            Object currentValue = getMBeanAttribute(mBeanServerConnection, metric);
            if (metric.getValue() instanceof Double && currentValue instanceof Double)
                validator.checkThreshold((double) currentValue, metric);
            else if (metric.getValue() instanceof Double && currentValue instanceof Integer)
                validator.checkThreshold((int) currentValue, metric);
        }
    }

    private static Object getMBeanAttribute(MBeanServerConnection mBeanServerConnection, Metric metric) throws Exception {
        ObjectName mObjectName = new ObjectName(metric.getObjectName());
        Set<ObjectName> names = mBeanServerConnection.queryNames(mObjectName, null);
        ObjectName on = (ObjectName) names.toArray()[0];
        return mBeanServerConnection.getAttribute(on, metric.getAttribute());
    }
}