package top.nilaoda.environment.main;

import top.nilaoda.environment.server.Server;
import top.nilaoda.environment.util.Configuration;
import top.nilaoda.environment.util.ConfigurationImpl;

/**
 * 服务器端入口类
 *
 * @author briup
 */
public class ServerMain {
    public static void main(String[] args) throws Exception {
        try {
			//实例化配置对象（包含了所有所需对象）
            Configuration conf = new ConfigurationImpl();
            Server server = conf.getServer();
            server.receiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
