package de.lgohlke;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BandwidthStatisticsTest {
    @Test
    void should_have_kbits() {
        var kbit = 2;
        var bytes = 1024 * kbit / 8;
        var stats = new BandwidthStatistics(bytes, 0);

        assertThat(stats.kbits()).isEqualTo(kbit);
    }

    @Test
    void should_have_kbits_per_second() {
        var kbit = 8;
        var ms = 1000;

        var stats = new BandwidthStatistics(1024 * kbit / 8, ms);

        assertThat(stats.bandwidth_kbits_per_second()).isEqualTo(kbit);
    }

    @Test
    void should_have_kbytes_per_second() {
        var kbit = 8;
        var ms = 1000;

        var stats = new BandwidthStatistics(1024 * kbit / 8, ms);

        assertThat(stats.bandwidth_kbytes_per_second()).isEqualTo(kbit / 8);
    }
}