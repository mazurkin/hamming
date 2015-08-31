package org.sample.benchmarks;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Timeout(time = 60, timeUnit = TimeUnit.MINUTES)
@Threads(value = 1)
@Warmup(iterations = 2, batchSize = 1000)
@Measurement(iterations = 5, batchSize = 1000)
@Fork(value = 1, warmups = 0, jvmArgs = {"-Xms2048m", "-Xmx2048m", "-XX:MaxDirectMemorySize=512M"})
public class PlainBenchmark {

    private static final long RANDOM_SEED = 1L;

    @Param({"1", "8"})
    public int distance;

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({"10000000"})
        public int capacity;

        public long[] values;

        @Setup(Level.Trial)
        public void init() {
            this.values = new long[capacity];

            Random initRandom = new Random(RANDOM_SEED);

            for (int i = 0; i < capacity; i++) {
                this.values[i] = initRandom.nextLong();
            }
        }

        @TearDown(Level.Trial)
        public void destroy() {
            this.values = null;
        }
    }

    @State(Scope.Benchmark)
    public static class ThreadState {

        public Random random;

        @Setup(Level.Iteration)
        public void init() {
            this.random = new Random(RANDOM_SEED + 1);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public int count(BenchmarkState bs, ThreadState ts) {
        long value = ts.random.nextLong();

        long[] values = bs.values;
        int size = bs.capacity;

        int distance = this.distance;

        int counter = 0;

        for (int i = 0; i < size; i++) {
            long xor = value ^ values[i];
            int d = Long.bitCount(xor);
            if (d <= distance) {
                counter++;
            }
        }

        return counter;
    }

    public static void main(String[] args) throws RunnerException {
        // Простейшие настройки - для отладки из IDE
        Options opt = new OptionsBuilder()
                .include(PlainBenchmark.class.getSimpleName())
                .forks(0)
                .warmupIterations(0)
                .measurementIterations(1)
                .threads(1)
                .measurementBatchSize(100)
                .build();

        new Runner(opt).run();
    }

}
