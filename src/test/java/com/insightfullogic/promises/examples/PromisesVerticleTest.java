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

import com.insightfullogic.promises.PromiseContainer;
import com.insightfullogic.promises.PromiseEventBus;
import com.insightfullogic.promises.PromiseVertx;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

/**
 * @author richard
 *
 */
public class PromisesVerticleTest extends TestVerticle {

	private static final String Q = "whats up ";

	// Collects messages from 'a' and 'b', send them to 'c' and check response
	@Test
	public void notNestingOfDoom() {
		final PromiseVertx vertx = new PromiseVertx(this.vertx);
		final PromiseEventBus bus = vertx.promiseBus();
		final PromiseContainer container = new PromiseContainer(this.container);
        container.deployVerticle(StubVerticle.class.getName())
        .bind(from -> {
            assertTrue(from.succeeded());
            return bus.send("a", Q + "a");
        }).compose(from ->
            bus.send("b", Q + "b")
        , (left, right) ->
            bus.send("c", left.body() + " " + right.body())
        ).then(cReply -> {
            assertEquals("hello world", cReply.body());
            testComplete();
        });
	}

    @Test
    public void replyNestingOfDoom() {
        final PromiseVertx vertx = new PromiseVertx(this.vertx);
        final PromiseContainer container = new PromiseContainer(this.container);

        vertx.promiseBus()
             .registerHandler("testVerticle", event -> {
                assertEquals("abc", event.body());
                testComplete();
             }).bind(from ->
                container.deployVerticle(SenderVerticle.class.getName())
             ).then(event ->
                assertTrue(event.succeeded())
             );
    }

}
