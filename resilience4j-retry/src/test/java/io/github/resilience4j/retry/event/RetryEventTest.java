/*
 *
 *  Copyright 2016 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.github.resilience4j.retry.event;

import org.junit.Test;

import java.io.IOException;

import static io.github.resilience4j.retry.event.RetryEvent.Type;
import static org.assertj.core.api.Assertions.assertThat;

public class RetryEventTest {

    @Test
    public void testRetryOnErrorEvent() {
        RetryOnErrorEvent retryOnErrorEvent = new RetryOnErrorEvent("test", 2,
                new IOException("Bla"));
        assertThat(retryOnErrorEvent.getName()).isEqualTo("test");
        assertThat(retryOnErrorEvent.getNumberOfRetryAttempts()).isEqualTo(2);
        assertThat(retryOnErrorEvent.getEventType()).isEqualTo(Type.ERROR);
        assertThat(retryOnErrorEvent.getLastThrowable()).isInstanceOf(IOException.class);
        assertThat(retryOnErrorEvent.toString()).contains("Retry 'test' recorded a failed retry attempt. Number of retry attempts: '2'. Giving up. Last exception was: 'java.io.IOException: Bla'.");
    }

    @Test
    public void testRetryOnSuccessEvent() {
        RetryOnSuccessEvent retryOnSuccessEvent = new RetryOnSuccessEvent("test", 2,
                new IOException("Bla"));
        assertThat(retryOnSuccessEvent.getName()).isEqualTo("test");
        assertThat(retryOnSuccessEvent.getNumberOfRetryAttempts()).isEqualTo(2);
        assertThat(retryOnSuccessEvent.getEventType()).isEqualTo(Type.SUCCESS);
        assertThat(retryOnSuccessEvent.getLastThrowable()).isInstanceOf(IOException.class);
        assertThat(retryOnSuccessEvent.toString()).contains("Retry 'test' recorded a successful retry attempt. Number of retry attempts: '2', Last exception was: 'java.io.IOException: Bla'.");
    }

    @Test
    public void testRetryOnIgnoredErrorEvent() {
        RetryOnIgnoredErrorEvent retryOnIgnoredErrorEvent = new RetryOnIgnoredErrorEvent("test",new IOException("Bla"));
        assertThat(retryOnIgnoredErrorEvent.getName()).isEqualTo("test");
        assertThat(retryOnIgnoredErrorEvent.getNumberOfRetryAttempts()).isEqualTo(0);
        assertThat(retryOnIgnoredErrorEvent.getEventType()).isEqualTo(Type.IGNORED_ERROR);
        assertThat(retryOnIgnoredErrorEvent.getLastThrowable()).isInstanceOf(IOException.class);
        assertThat(retryOnIgnoredErrorEvent.toString()).contains("Retry 'test' recorded an error which has been ignored: 'java.io.IOException: Bla'.");
    }

}
