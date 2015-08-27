package org.sample;

import java.util.concurrent.atomic.AtomicInteger;

final class HammL3Stat {

    final AtomicInteger capacity;

    final AtomicInteger reallocs;

    final AtomicInteger moves;

    public HammL3Stat() {
        this.capacity = new AtomicInteger(0);
        this.reallocs = new AtomicInteger(0);
        this.moves = new AtomicInteger(0);
    }

}
