package org.soichiro.nettystudy.example.time;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class TimeServer {

  final static ChannelGroup allChannels = new DefaultChannelGroup("time-server");
  
  public static void main(String[] args) throws Exception {
	  
      ChannelFactory factory =
          new NioServerSocketChannelFactory(
                  Executors.newCachedThreadPool(),
                  Executors.newCachedThreadPool());

      ServerBootstrap bootstrap = new ServerBootstrap(factory);

      bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
          public ChannelPipeline getPipeline() {
              return Channels.pipeline(new TimeServerHandler(),
            		  new TimeEncoder());
          }
      });

      bootstrap.setOption("child.tcpNoDelay", true);
      bootstrap.setOption("child.keepAlive", true);

      Channel channel = bootstrap.bind(new InetSocketAddress(8080));
      allChannels.add(channel);
      waitForShutdownCommand();
      ChannelGroupFuture future = allChannels.close();
      future.awaitUninterruptibly();
      factory.releaseExternalResources();
  }
  
  private static final CountDownLatch shutdownSignal = new CountDownLatch(1);
  private static void waitForShutdownCommand() {
	  Thread shutdown = new Thread() {
	      public void run() { 
	    	  shutdownSignal.countDown();
	      }
	  };
	  Runtime.getRuntime().addShutdownHook(shutdown);
	  try {
		shutdownSignal.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }
}
