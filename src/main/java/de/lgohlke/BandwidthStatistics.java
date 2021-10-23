package de.lgohlke;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
@Data
class BandwidthStatistics {
    private final long bytes;
    private final long milliseconds;

    public long kbits() {
        return bytes * 8 / 1024;
    }

    long bandwidth_kbits_per_second() {
        val duration = milliseconds == 0 ? 1 : milliseconds;
        return kbits() * 1000 / duration;
    }

    long bandwidth_kbytes_per_second() {
        val duration = milliseconds == 0 ? 1 : milliseconds;
        return bytes * 1000 / duration / 1024;
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
