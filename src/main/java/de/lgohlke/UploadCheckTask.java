package de.lgohlke;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class UploadCheckTask extends Task {
    private final DataLimitExceededTask dataLimitExceededTask;
    private final String host;
    private final int port;

    public UploadCheckTask(DataLimitExceededTask dataLimitExceededTask) {
        this.dataLimitExceededTask = dataLimitExceededTask;
        log.info(" checking  ...");
        var host_port = System.getenv("UPLOAD_HOST_PORT");
        Objects.requireNonNull(host_port, "missing 'host:port' to check upload speed: UPLOAD_HOST_PORT");
        var parts = host_port.split(":");
        host = parts[0];
        port = Integer.parseInt(parts[1]);
    }

    @Override
    public void doTask() {
        if (is_throttled()) {
            log.info("Drosselung");
            dataLimitExceededTask.orderVolume();
        }
    }

    boolean is_throttled() {
        try {
            val stats = sendRequestWithKbits(30);
            log.info("duration: " + stats);
            return stats.getMilliseconds() > 200;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private BandwidthStatistics sendRequestWithKbits(int kbits) throws IOException {
        var bytes = createByteArray(kbits * 1024 / 8);
        var start = System.currentTimeMillis();
        var socket = new Socket(host, port);
        var end = System.currentTimeMillis();
        var timeToConnect = end - start;
        log.debug("time to connect: {}ms", timeToConnect);
        return upload(bytes, socket);
    }

    private byte[] createByteArray(int count) {
        var randomString = new Random().ints(0, 10)
                                       .limit(count)
                                       .boxed()
                                       .map(i -> Integer.toString(i))
                                       .collect(Collectors.joining());
        return ("uploadcheck-" + randomString).getBytes(StandardCharsets.UTF_8);
    }

    private static BandwidthStatistics upload(byte[] bytes, Socket socket) {
        var startSend = System.currentTimeMillis();
        int count = 0;
        try (var outputStream = socket.getOutputStream()) {
            for (byte aByte : bytes) {
                outputStream.write(aByte);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BandwidthStatistics(count, System.currentTimeMillis() - startSend);
    }

}
