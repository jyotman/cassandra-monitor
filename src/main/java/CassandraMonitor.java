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
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CassandraMonitor {

    public static void main(String args[]) throws ScriptException, MalformedURLException, FileNotFoundException {

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

    private static Config loadConfig() throws ScriptException, FileNotFoundException {
        String path = System.getenv("CONFIG");
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String jsonString = String.join("\n", reader.lines().collect(Collectors.toList())).replaceAll("\n", "").replaceAll(" ", "");
        return new Gson().fromJson(jsonString, Config.class);
    }

    private static void validate(Config config, MBeanServerConnection mBeanServerConnection, Node node) {
        Validator validator = new Validator(config, node);
        validator.checkHosts((List) getMBeanAttribute(mBeanServerConnection, "org.apache.cassandra.db:type=StorageService", "LiveNodes"));
        for (Metric metric : config.getMetrics()) {
            validator.checkMaxThreshold((double) getMBeanAttribute(mBeanServerConnection, metric.getObjectName(), metric.getAttribute()), metric.getValue(), metric.getObjectName());
        }
    }

    private static Object getMBeanAttribute(MBeanServerConnection mBeanServerConnection, String objectName, String attribute) {
        try {
            ObjectName mObjectName = new ObjectName(objectName);
            Set<ObjectName> names = mBeanServerConnection.queryNames(mObjectName, null);
            ObjectName on = (ObjectName) names.toArray()[0];
            return mBeanServerConnection.getAttribute(on, attribute);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}