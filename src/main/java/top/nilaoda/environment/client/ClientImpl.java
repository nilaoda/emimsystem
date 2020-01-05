package top.nilaoda.environment.client;

import top.nilaoda.environment.bean.Environment;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

/**
 * @author nilaoda
 * @version 1.0
 * @description 客户端发送数据给服务端
 * @date 2019/10/29
 * @time 16:16
 */
public class ClientImpl implements Client {
    private String ip = "";
    private int port;

    @Override
    public void send(Collection<Environment> coll) throws Exception {
        //创建Socket，与服务器连接
        Socket s = new Socket(ip, port);
        //获取流对象
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        //写出数据
        oos.writeObject(coll);

        //释放资源
        if (oos != null)
            oos.close();
        if (s != null)
            s.close();
    }

    @Override
    public void init(Properties properties) throws Exception {
        ip = properties.getProperty("client_ip");
        port = Integer.parseInt(properties.getProperty("server_port"));
    }
}
