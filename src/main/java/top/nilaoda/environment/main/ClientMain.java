package top.nilaoda.environment.main;

import top.nilaoda.environment.client.Client;
import top.nilaoda.environment.client.Gather;
import top.nilaoda.environment.util.Configuration;
import top.nilaoda.environment.util.ConfigurationImpl;

/**
 * 客户端端入口类
 *
 * @author briup
 */
public class ClientMain {
    public static void main(String[] args) throws Exception {
        try {
        	//实例化配置对象（包含了所有所需对象）
            Configuration conf = new ConfigurationImpl();
            Client client = conf.getClient();
            Gather gather = conf.getGather();
            client.send(gather.gather());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
