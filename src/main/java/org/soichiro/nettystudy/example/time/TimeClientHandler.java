package org.soichiro.nettystudy.example.time;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class TimeClientHandler extends SimpleChannelHandler {

	 @Override
	 public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
	     UnixTime m = (UnixTime) e.getMessage();
	     System.out.println(m);
	     e.getChannel().close();
	 }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
