package com.biasedbit.efflux.network;

import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

public class ControlChannelPipelineFactory implements ChannelPipelineFactory {

  private final Optional<Executor> executor;
  private final DataPacketReceiver receiver;

  public ControlChannelPipelineFactory(@Nonnull Executor executor, @Nonnull DataPacketReceiver receiver) {
    this(Optional.of(executor), receiver);
  }

  public ControlChannelPipelineFactory(@Nonnull DataPacketReceiver receiver) {
    this(Optional.<Executor>absent(), receiver);
  }

  private ControlChannelPipelineFactory(@Nonnull Optional<Executor> executor, @Nonnull DataPacketReceiver receiver) {
    this.executor = executor;
    this.receiver = receiver;
  }

  @Override
  public ChannelPipeline getPipeline() throws Exception {
    ChannelPipeline pipeline = Channels.pipeline();
    pipeline.addLast("decoder", new DataPacketDecoder());
    pipeline.addLast("encoder", DataPacketEncoder.getInstance());
    if (executor.isPresent()) {
      pipeline.addLast("executorHandler", new ExecutionHandler(executor.get()));
    }
    pipeline.addLast("handler", new DataHandler(receiver));
    return pipeline;
  }
}
