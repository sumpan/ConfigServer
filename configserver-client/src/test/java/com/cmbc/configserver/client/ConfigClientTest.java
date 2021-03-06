package com.cmbc.configserver.client;

import com.cmbc.configserver.client.impl.ConfigClientImpl;
import com.cmbc.configserver.domain.Configuration;
import com.cmbc.configserver.remoting.netty.NettyClientConfig;
import com.cmbc.configserver.utils.PathUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tongchuan.lin<linckham@gmail.com><br/>
 *
 * @author tongchuan.lin<linckham@gmail.com>.
 *         Date: 2014/10/23
 *         Time: 13:51
 */
public class ConfigClientTest {
    private ConfigClient configClient;

    @Before
    public void initialize() throws InterruptedException {
        NettyClientConfig nettyClientConfig = new NettyClientConfig();
        List<String> configServerAddress = new ArrayList<String>(1);
        configServerAddress.add("127.0.0.1:19999");
        this.configClient = new ConfigClientImpl(nettyClientConfig, configServerAddress, null);
        System.out.println("---the config client initialized successfully!---");
    }

    @Test
    public void testPublish() {
        Configuration config = new Configuration();
        String content = "{\"ip\":\"127.0.0.1\",\"port\":21881,\"meta\":\"just for test\"}";
        config.setContent(content);
        config.setCell("test-cell");
        config.setResource("test-message-queue");
        config.setType("producers");
        long start = System.currentTimeMillis();
        this.configClient.publish(config);
        System.out.println(String.format("the consuming time of  publish config is %s ms", System.currentTimeMillis() - start));
    }

    @Test
    public void testPubSub() {
        Configuration config = new Configuration();
        String content = "{\"ip\":\"127.0.0.1\",\"port\":21881,\"meta\":\"just for test\"}";
        config.setContent(content);

        config.setCell("test-cell");
        config.setResource("test-dubbo-rpc");
        config.setType("publisher");
        long start = System.currentTimeMillis();
        this.configClient.publish(config);
        System.out.println(String.format("the consuming time of  publish config is %s ms", System.currentTimeMillis() - start));

        final Configuration subConfig = new Configuration();
        subConfig.setCell("test-cell");
        subConfig.setResource("test-dubbo-rpc");
        subConfig.setType("publisher");
        this.configClient.subscribe(config, new ResourceListener() {
            public void notify(List<Configuration> configs) {
                if (null != configs && !configs.isEmpty()) {
                    System.out.println(String.format("the subscribe path %s has %s configuration items,", PathUtils.getSubscriberPath(subConfig), configs.size()));
                    for (Configuration item : configs){
                        System.out.println(item);
                    }
                }
                else
                {
                    System.out.println("the configs is empty.");
                }
            }
        });
    }

    @After
    public void destroy() {
        if (null != configClient) {
            System.out.println("---the config client destroy successfully!---");
        }
    }
}
