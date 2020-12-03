package netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.http.route.RouteMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author RAY
 * @descriptions
 * @since 2020/3/29
 */
public class HttpServer {

    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private static final int port = 8088;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        final HttpServerHandler serverHandler = new HttpServerHandler();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup) //设置链接和工作线程组
                    .channel(NioServerSocketChannel.class)  //设置服务端
                    .option(ChannelOption.SO_BACKLOG, 128) //设置客户端连接队列的链接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动链接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            pipeline.addLast(new HttpRequestDecoder())
                                    .addLast(new HttpResponseEncoder())
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpObjectAggregator(1024 * 64))
                                    .addLast(serverHandler);
                        }
                    });  //给workerGroup的 EventLoop对应的管道设置处理器
            logger.info("服务器 is ready。。。");
            //启动服务器绑定端口 用作http长连接的话，端口最好取80开头的，例如6667就不行
            ChannelFuture fu = bootstrap.bind(port).sync();
            fu.addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("监听端口" + port + "成功");
                } else {
                    logger.info("监听失败");
                }
            });
            RouteMethod.init();
            long end = System.currentTimeMillis();
            logger.info("the launch cost {} millis", (end - start));
            //监听通道
            fu.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

