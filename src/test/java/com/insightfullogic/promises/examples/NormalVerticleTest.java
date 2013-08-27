/*
 * Copyright 2013 Richard Warburton <richard.warburton@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.insightfullogic.promises.examples;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.testtools.TestVerticle;

/**
 * @author richard
 *
 */
public class NormalVerticleTest extends TestVerticle {

	private static final String Q = "whatsUp";

	// Collects messages from 'a' and 'b', send them to 'c' and check response
	@Test
	public void nestingOfDoom() {
        container.deployVerticle(StubVerticle.class.getName(), event -> {
            assertTrue(event.succeeded());
            final EventBus eb = vertx.eventBus();
            eb.send("a", Q, (Message<String> aReply) -> {
                final String a = aReply.body();
                eb.send("b", Q, (Message<String> bReply) -> {
                    final String b = bReply.body();
                    String message = a + " " + b;
                    eb.send("c", message, (Message<String> cReply) -> {
                        assertEquals("hello world", cReply.body());
                        testComplete();
                    });
                });
            });
        });
	}
j
	@Test
	public void replyNestingOfDoom() {
		final EventBus eb = vertx.eventBus();
		eb.registerHandler("testVerticle", (Message<String> event) -> {
            assertEquals("abc", event.body());
            testComplete();
		}, (AsyncResult<Void> result) -> {
            assertTrue(result.succeeded());
            container.deployVerticle(SenderVerticle.class.getName(), (AsyncResult<String> event) -> {
                assertTrue(event.succeeded());
            });
		});
	}

}
