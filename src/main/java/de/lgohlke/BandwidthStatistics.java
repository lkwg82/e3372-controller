package de.lgohlke;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
class BandwidthStatistics {
    private final long bytes;
    private final long milliseconds;

    public long kbits() {
        return bytes * 8 / 1024;
    }

    long bandwidth_kbits_per_second() {
        return kbits() * 1000 / milliseconds;
    }

    long bandwidth_kbytes_per_second() {
        return bytes * 1000 / milliseconds / 1024;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "bytes=" + bytes + " bytes" +
                ", kbits=" + kbits() + " kB" +
                ", kbytes=" + kbits() / 8 + " kb" +
                ", duration=" + milliseconds + " ms" +
                ", bandwidth=" + bandwidth_kbytes_per_second() + " kbyte/s" +
                ", bandwidth=" + bandwidth_kbits_per_second() + " kbit/s" +
                '}';
    }
}
