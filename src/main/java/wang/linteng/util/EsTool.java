package wang.linteng.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EsTool {

    public static final String CLUSTER_NAME ="linteng-es";
    public static final String INDEX_NAME = "news";

    private static TransportClient client;

    // 获取客户端
    public static TransportClient getClient(){
        Settings settings = Settings.builder()
                .put("cluster.name",CLUSTER_NAME)
                .put("client.transport.sniff", true)
                .build();
        client = new PreBuiltTransportClient(settings);
        try {
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }

    // 关闭客户端
    public static void closeClient(){
        if(client != null){
            client.close();
        }
    }

    // 获取索引名称
    public static String getIndexName(){
        return INDEX_NAME;
    }

}
