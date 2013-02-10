package com.biasedbit.efflux.network;

import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

public class DataChannelPipelineFactory implements ChannelPipelineFactory {

  private final Optional<Executor> executor;
  private final ControlPacketReceiver receiver;

  public DataChannelPipelineFactory(@Nonnull Executor executor, @Nonnull ControlPacketReceiver receiver) {
    this(Optional.of(executor), receiver);
  }

  public DataChannelPipelineFactory(@Nonnull ControlPacketReceiver receiver) {
    this(Optional.<Executor>absent(), receiver);
  }

  private DataChannelPipelineFactory(@Nonnull Optional<Executor> executor, @Nonnull ControlPacketReceiver receiver) {
    this.executor = executor;
    this.receiver = receiver;
  }

  @Override
  public ChannelPipeline getPipeline() throws Exception {
    ChannelPipeline pipeline = Channels.pipeline();
    pipeline.addLast("decoder", new ControlPacketDecoder());
    pipeline.addLast("encoder", ControlPacketEncoder.getInstance());
    if (executor.isPresent()) {
      pipeline.addLast("executorHandler", new ExecutionHandler(executor.get()));
    }
    pipeline.addLast("handler", new ControlHandler(receiver));
    return pipeline;
  }
}
