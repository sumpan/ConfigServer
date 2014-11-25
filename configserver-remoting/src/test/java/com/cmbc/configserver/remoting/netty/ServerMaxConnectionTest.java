package com.cmbc.configserver.remoting.netty;

import com.cmbc.configserver.common.ThreadFactoryImpl;
import com.cmbc.configserver.remoting.ChannelEventListener;
import com.cmbc.configserver.remoting.RemotingClient;
import com.cmbc.configserver.remoting.RemotingServer;
import com.cmbc.configserver.remoting.common.RemotingHelper;
import com.cmbc.configserver.remoting.common.RequestProcessor;
import com.cmbc.configserver.remoting.exception.RemotingConnectException;
import com.cmbc.configserver.remoting.exception.RemotingSendRequestException;
import com.cmbc.configserver.remoting.exception.RemotingTimeoutException;
import com.cmbc.configserver.remoting.protocol.RemotingCommand;
import com.cmbc.configserver.utils.Constants;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * the test case use to test the max connection limit
 * Created by tongchuan.lin<linckham@gmail.com><br/>
 *
 * @Date 2014/11/12
 * @Time 11:12
 */
public class ServerMaxConnectionTest {
    private final int maxConnectionNumber = 10;

    private RemotingClient createRemoteClient() {
        NettyClientConfig clientConfig = new NettyClientConfig();
        clientConfig.setConnectTimeoutMillis(2 * 1000);
        clientConfig.setClientChannelMaxIdleTimeSeconds(10 * 1000);
        RemotingClient client = new NettyRemotingClient(clientConfig);
        List<String> server = new ArrayList<String>();
        server.add("127.0.0.1:19999");
        ((NettyRemotingClient) client).updateNameServerAddressList(server);
        client.start();
        return client;
    }

    private RemotingServer createRemoteServer() {
        NettyServerConfig config = new NettyServerConfig();
        config.setListenPort(19999);
        config.setServerMaxConnectionNumbers(maxConnectionNumber);
        config.setServerChannelMaxIdleTimeSeconds(15 * 1000);
        RemotingServer server = new NettyRemotingServer(config, new ChannelEventListener() {

            @Override
            public void onChannelConnect(final NettyEvent event) {

            }

            @Override
            public void onChannelClose(final NettyEvent event) {
            }

            @Override
            public void onChannelException(final NettyEvent event) {
            }

            @Override
            public void onChannelIdle(final NettyEvent event) {

            }

            @Override
            public void onChannelActive(final NettyEvent event) {
            }
        });
        server.registerProcessor(0, new RequestProcessor() {
            private int requestCount = 0;

            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
                System.out.println("processRequest=" + request + " " + (requestCount++));
                String body = "hello, I am response " + RemotingHelper.getChannelId(ctx.channel());
                request.setBody(body.getBytes());
                return request;
            }
        }, Executors.newCachedThreadPool(new ThreadFactoryImpl("netty-server-thread")));
        server.start();
        return server;
    }

    @Test
    public void testMaxConnectionNumber() {
        RemotingServer server = this.createRemoteServer();
        Assert.assertNotNull(server);
        Assert.assertEquals(19999, server.localPort());
        List<RemotingClient> clientList = new ArrayList<RemotingClient>();
        for (int i = 0; i < maxConnectionNumber; i++) {
            RemotingClient client = this.createRemoteClient();
            clientList.add(client);
            RemotingCommand request = RemotingCommand.createRequestCommand(0);
            request.setBody(null);
            try {
                client.invokeSync(request, Constants.DEFAULT_SOCKET_READING_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemotingConnectException e) {
                e.printStackTrace();
            } catch (RemotingSendRequestException e) {
                e.printStackTrace();
            } catch (RemotingTimeoutException e) {
                e.printStackTrace();
            }
        }

        RemotingClient tmpClient = this.createRemoteClient();

        for (RemotingClient client : clientList) {
            client.shutdown();
        }

        tmpClient.shutdown();

    }
}
