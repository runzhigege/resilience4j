/*
 *
 *  Copyright 2019 Robert Winkler and Bohdan Storozhuk
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
package io.github.resilience4j.core.metrics;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedSizeSlidingWindowMetricsTest {

    @Test
    public void checkInitialBucketCreation(){
        FixedSizeSlidingWindowMetrics metrics = new FixedSizeSlidingWindowMetrics(5);

        Snapshot snapshot = metrics.getSnapshot();

        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(0);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(0);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(0);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(0);
    }

    @Test
    public void testRecordSuccess(){
        Metrics metrics = new FixedSizeSlidingWindowMetrics(5);

        Snapshot snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);
        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(0);
        assertThat(snapshot.getNumberOfSlowCalls()).isEqualTo(0);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(0);
    }

    @Test
    public void testRecordError(){
        Metrics metrics = new FixedSizeSlidingWindowMetrics(5);

        Snapshot snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.ERROR);
        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(0);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfSlowCalls()).isEqualTo(0);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(100);
    }

    @Test
    public void testRecordSlowSuccess(){
        Metrics metrics = new FixedSizeSlidingWindowMetrics(5);

        Snapshot snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SLOW_SUCCESS);
        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(0);
        assertThat(snapshot.getNumberOfSlowCalls()).isEqualTo(1);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(0);
    }

    @Test
    public void testSlowCallsPercentage(){
        Metrics metrics = new FixedSizeSlidingWindowMetrics(5);

        metrics.record(10000, TimeUnit.MILLISECONDS, Metrics.Outcome.SLOW_SUCCESS);
        metrics.record(10000, TimeUnit.MILLISECONDS, Metrics.Outcome.SLOW_ERROR);
        metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);
        metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);
        metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);

        Snapshot snapshot = metrics.getSnapshot();
        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(5);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(4);
        assertThat(snapshot.getNumberOfSlowCalls()).isEqualTo(2);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(20300);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(4060);
        assertThat(snapshot.getSlowCallRate()).isEqualTo(40f);
    }

    @Test
    public void testMoveHeadIndexByOne(){
        FixedSizeSlidingWindowMetrics metrics = new FixedSizeSlidingWindowMetrics(3);

        assertThat(metrics.headIndex).isEqualTo(0);

        metrics.moveHeadIndexByOne();

        assertThat(metrics.headIndex).isEqualTo(1);

        metrics.moveHeadIndexByOne();

        assertThat(metrics.headIndex).isEqualTo(2);

        metrics.moveHeadIndexByOne();

        assertThat(metrics.headIndex).isEqualTo(0);

        metrics.moveHeadIndexByOne();

        assertThat(metrics.headIndex).isEqualTo(1);

    }

    @Test
    public void testSlidingWindowMetrics(){
        FixedSizeSlidingWindowMetrics metrics = new FixedSizeSlidingWindowMetrics(4);

        Snapshot snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.ERROR);
        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(0);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(100);

        snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);
        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(2);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(1);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(200);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(50);

        snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);
        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(3);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(2);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(300);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(33.333332f);

        snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);

        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(4);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(3);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(400);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(25);

        snapshot = metrics.record(100, TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS);

        assertThat(snapshot.getTotalNumberOfCalls()).isEqualTo(4);
        assertThat(snapshot.getNumberOfSuccessfulCalls()).isEqualTo(4);
        assertThat(snapshot.getNumberOfFailedCalls()).isEqualTo(0);
        assertThat(snapshot.getTotalDuration().toMillis()).isEqualTo(400);
        assertThat(snapshot.getAverageDuration().toMillis()).isEqualTo(100);
        assertThat(snapshot.getFailureRate()).isEqualTo(0);
    }
}
