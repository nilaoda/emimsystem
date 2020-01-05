package top.nilaoda.environment.util;

import top.nilaoda.environment.client.Client;
import top.nilaoda.environment.client.Gather;
import top.nilaoda.environment.server.DBStore;
import top.nilaoda.environment.server.Server;

import java.util.Map;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {
    private static Map<String, Object> map;
    private static Properties properties;

    public ConfigurationImpl() {
        map = new ConfigurationReader().readObjects();
        properties = new ConfigurationReader().readConfigAsProperties();

        //确保各个实现类中的调用关系，先setConfiguration，再init
        // (因为有的init中调用了configuration来获取logger)
        for (Object o : map.values()) {
            if (o instanceof ConfigurationAWare) {
                try {
                    ((ConfigurationAWare) o).setConfiguration(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (o instanceof WossModule) {
                try {
                    ((WossModule) o).init(properties);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Log getLogger() throws Exception {
        return (Log) map.get("logger");
    }

    @Override
    public Server getServer() throws Exception {
        return (Server) map.get("server");
    }

    @Override
    public Client getClient() throws Exception {
        return (Client) map.get("client");
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return (DBStore) map.get("dbstore");
    }

    @Override
    public Gather getGather() throws Exception {
        return (Gather) map.get("gather");
    }

    @Override
    public Backup getBackup() throws Exception {
        return (Backup) map.get("backup");
    }
}
