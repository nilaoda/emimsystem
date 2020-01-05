package top.nilaoda.environment.server;

import top.nilaoda.environment.bean.Environment;
import top.nilaoda.environment.util.Configuration;
import top.nilaoda.environment.util.ConfigurationAWare;
import top.nilaoda.environment.util.Log;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nilaoda
 * @version 1.0
 * @description 服务端接收数据
 * @date 2019/10/29
 * @time 16:16
 */
public class ServerImpl implements Server, ConfigurationAWare {
    private boolean flag = false;
    private Log logger;
    private int port;
    private Configuration configuration;

    @Override
    public void receiver() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        //线程池对象
        ExecutorService pool = Executors.newFixedThreadPool(10);

        logger.info("服务器开始监听");
        while (!flag) {
            //接受客户端连接
            Socket s = serverSocket.accept();

            //子线程为当前客户端提供服务
            pool.submit(() -> {
                try {
                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                    Collection<Environment> environments = (Collection<Environment>) ois.readObject();
                    logger.debug(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date())
                            + " 接收到environments共 " + environments.size() + " 条");
                    //入库
                    configuration.getDbStore().saveDb(environments);

                    //释放资源
                    if (s != null)
                        s.close();
                    if (ois != null)
                        ois.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        pool.shutdown();

        if (serverSocket != null)
            serverSocket.close();
    }

    @Override
    public void init(Properties properties) throws Exception {
        port = Integer.parseInt(properties.getProperty("server_port"));
        logger = configuration.getLogger();
    }

    public void shutdown() {
        flag = !flag;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
