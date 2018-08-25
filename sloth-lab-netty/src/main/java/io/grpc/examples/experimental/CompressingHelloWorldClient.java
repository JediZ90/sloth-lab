/*
 * Copyright 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.grpc.examples.experimental;

import com.google.common.util.concurrent.Uninterruptibles;

import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ClientCall.Listener;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.internal.GrpcUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the
 * {@link io.grpc.examples.helloworld.HelloWorldServer}.
 *
 * <p>
 * This class should act a a drop in replacement for
 * {@link io.grpc.examples.helloworld.HelloWorldClient}.
 */
public class CompressingHelloWorldClient {
    private static final Logger logger = Logger.getLogger(CompressingHelloWorldClient.class.getName());

    private final ManagedChannel channel;

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public CompressingHelloWorldClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void greet(final String name) {
        final ClientCall<HelloRequest, HelloReply> call = channel.newCall(GreeterGrpc.METHOD_SAY_HELLO,
                        CallOptions.DEFAULT);

        final CountDownLatch latch = new CountDownLatch(1);

        call.start(new Listener<HelloReply>() {
            @Override
            public void onHeaders(Metadata headers) {
                super.onHeaders(headers);
                String encoding = headers.get(GrpcUtil.MESSAGE_ENCODING_KEY);
                if (encoding == null) {
                    throw new RuntimeException("No compression selected!");
                }
            }

            @Override
            public void onMessage(HelloReply message) {
                super.onMessage(message);
                logger.info("Greeting: " + message.getMessage());
                latch.countDown();
            }

            @Override
            public void onClose(Status status, Metadata trailers) {
                latch.countDown();
                if (!status.isOk()) {
                    throw status.asRuntimeException();
                }
            }
        }, new Metadata());

        call.setMessageCompression(true);
        call.sendMessage(HelloRequest.newBuilder().setName(name).build());
        call.request(1);
        call.halfClose();

        Uninterruptibles.awaitUninterruptibly(latch, 100, TimeUnit.SECONDS);
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name
     * to use in the greeting.
     */
    public static void main(String[] args) throws Exception {
        CompressingHelloWorldClient client = new CompressingHelloWorldClient("localhost", 50051);
        try {
            /* Access a service running on the local machine on port 50051 */
            String user = "world";
            if (args.length > 0) {
                user = args[0]; /*
                                 * Use the arg as the name to greet if provided
                                 */
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}
