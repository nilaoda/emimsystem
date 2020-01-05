import top.nilaoda.environment.bean.Environment;
import top.nilaoda.environment.client.Client;
import top.nilaoda.environment.client.ClientImpl;
import top.nilaoda.environment.client.Gather;
import top.nilaoda.environment.client.GatherImpl;
import top.nilaoda.environment.server.DBStore;
import top.nilaoda.environment.server.DBStoreImpl;
import top.nilaoda.environment.server.ServerImpl;
import top.nilaoda.environment.util.BackupImpl;
import top.nilaoda.environment.util.Configuration;
import top.nilaoda.environment.util.ConfigurationImpl;
import top.nilaoda.environment.util.ConfigurationReader;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public class MainTest {
    private Configuration configuration;

    @Before
    public void init(){
        configuration = new ConfigurationImpl();
    }

    @Test
    public void GatherImplTest() throws Exception {
        GatherImpl gather = new GatherImpl();
        for (Environment e : gather.gather()) {
            System.out.println(e);
        }
    }

    @Test
    public void ServerReceiveTest() {
        Thread server = new Thread(() -> {
            try {
                ServerImpl s = new ServerImpl();
                s.receiver();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        server.start();


        Thread client = new Thread(() -> {
            try {
                Gather gather = new GatherImpl();
                Client client1 = new ClientImpl();
                client1.send(gather.gather());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        client.start();

        try {
            //等待线程都结束
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SaveDBTest() {
        try {
            DBStore dbStore = new DBStoreImpl();
            Gather gather = new GatherImpl();
            dbStore.saveDb(gather.gather());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ReadBackupTest() {
        BackupImpl backup = new BackupImpl();

        for (Environment e : (Collection<Environment>) backup.load("C:\\Users\\nilao\\Desktop\\EMIS_backup.data")) {
            System.out.println(e);
        }
    }

    @Test
    public void ReadConfigTest() {
        Map<String, Object> map = new ConfigurationReader().readObjects();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry);
        }
    }

    @Test
    public void ReadConfigTest2() {
        Map<String, String> map = new ConfigurationReader().readConfig();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry);
        }
    }

    @Test
    public void ReadConfigTest3() {
        Properties p = new ConfigurationReader().readConfigAsProperties();
        System.out.println(p);
    }

    @Test
    public void GatherImplTest2() throws Exception {
        Gather gather = configuration.getGather();
        for (Environment e : gather.gather()) {
            System.out.println(e);
        }
    }
}
