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
import org.sample.HammL3;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Timeout(time = 60, timeUnit = TimeUnit.MINUTES)
@Threads(value = 8)
@Warmup(iterations = 2, batchSize = 1000)
@Measurement(iterations = 5, batchSize = 1000)
@Fork(value = 1, warmups = 0, jvmArgs = {"-Xms2048m", "-Xmx2048m", "-XX:MaxDirectMemorySize=512M"})
public class HammL3Benchmark {

    private static final long RANDOM_SEED = 1L;

    @Param({"0", "2", "4", "8", "16"})
    public int distance;

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({"10000000"})
        public int capacity;

        public HammL3 hamm;

        @Setup(Level.Trial)
        public void init() {
            this.hamm = new HammL3();

            Random initRandom = new Random(RANDOM_SEED);

            for (int i = 0; i < capacity; i++) {
                this.hamm.add(initRandom.nextLong());
            }
        }

        @TearDown(Level.Trial)
        public void destroy() {
            this.hamm.destroy();
            this.hamm = null;
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
        return bs.hamm.count(ts.random.nextLong(), distance);
    }

    public static void main(String[] args) throws RunnerException {
        // Простейшие настройки - для отладки из IDE
        Options opt = new OptionsBuilder()
                .include(HammL3Benchmark.class.getSimpleName())
                .forks(0)
                .warmupIterations(0)
                .measurementIterations(1)
                .threads(1)
                .measurementBatchSize(100)
                .build();

        new Runner(opt).run();
    }

}
