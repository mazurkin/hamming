package org.sample;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Timeout(time = 60, timeUnit = TimeUnit.SECONDS)
@Threads(value = 8)
@Warmup(iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, warmups = 1, jvmArgs = {"-Xms2048m", "-Xmx2048m", "-XX:MaxDirectMemorySize=512M"})
public class HammL3Benchmark {

    private static final long RANDOM_SEED = 1L;

    @Param({"0", "1", "2", "4", "8", "16"})
    public int distance;

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({"50000000"})
        public int count;

        public HammL3 hamm;

        public BenchmarkState() {
            this.hamm = new HammL3();

            Random initRandom = new Random(RANDOM_SEED);

            for (int i = 0; i < count; i++) {
                this.hamm.add(initRandom.nextLong());
            }
        }

        @TearDown
        public void destroy() {
            this.hamm.destroy();
        }
    }

    @State(Scope.Thread)
    public static class ThreadState {

        public Random random;

        public ThreadState() {
            // Создаем ГСЧ с той же последовательностью что и при заполнении хранилища тестовыми хэшами. При этом
            // первые BenchmarkState.count вызовов мы будем гарантированно находить числа которые есть в хранилище, ну
            // а при последущих вызовах - как повезет
            this.random = new Random(RANDOM_SEED);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public boolean contains(BenchmarkState bs, ThreadState ts) {
        return bs.hamm.contains(ts.random.nextLong(), distance);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public int count(BenchmarkState bs, ThreadState ts) {
        return bs.hamm.count(ts.random.nextLong(), distance);
    }

}
