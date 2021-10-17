package de.lgohlke;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.lgohlke.hilink.APIErrorException;
import de.lgohlke.hilink.api.SMS;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
class DataLimitExceededTask extends Task {
    private final String redirectTo;

    DataLimitExceededTask() {
        log.info(" checking  ...");
        var redirectTo = System.getenv("REDIRECT_PHONE_NUMBER");
        Objects.requireNonNull(redirectTo, "missing number to redirect: REDIRECT_PHONE_NUMBER");
        this.redirectTo = redirectTo;
    }

    @SneakyThrows
    @Override
    public void doTask() {
        try {
            SMS.Actions.list(SMS.BOXTYPE.INBOX)
                       .forEach(message -> {
                           log.info(message.toString());

                           handleSMS(message);

                           var status = SMS.Actions.delete(message);
                           if (status.isOk()) {
                               log.info("removed {}", message.getIndex());
                           } else {
                               log.error(status.getStatus());
                           }
                       });

            SMS.Actions.list(SMS.BOXTYPE.OUTBOX)
                       .forEach(message -> {
                           var status = SMS.Actions.delete(message);
                           if (status.isOk()) {
                               log.info("removed {}", message.getIndex());
                           } else {
                               log.error(status.getStatus());
                           }
                       });
        } catch (Exception e) {
            if (e instanceof HttpConnectTimeoutException || e instanceof ConnectException) {
                log.error("http: " + e.getMessage());
                e.printStackTrace();
            } else if (e instanceof IOException) {
                log.warn(e.getMessage());
            } else if (e instanceof APIErrorException) {
                var apiErrorException = (APIErrorException) e;
                var error = apiErrorException.getError();
                log.warn("code {} name '{}' message '{}'",
                         error.getCode(),
                         error.getError_code(),
                         error.getMessage());
                TimeUnit.SECONDS.sleep(5);
            } else {
                throw e;
            }
        }
    }

    @Override
    protected void sleep_time() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
    }

    private void handleSMS(SMS.Response.List.Message message) {

        var type = message.getType();

        if (type == SMS.Response.List.Message.TYPE.STATUS && message.getStatus() == SMS.Response.List.Message.SMSTAT.READ) {
            System.out.println("SMS read");
        } else {
            var content = "received from " + message.getPhone() + "\n" + message.getContent();
            SMS.Actions.send(redirectTo, content);
        }
        switch (type) {
            case DATA_VOLUME_EXCEEDED:
                handleDataVolumeExceeded(message);
                break;
            case INCOMING:
                handleIncoming(message);
                break;
            default:
                handleUnknownType(message);
        }
    }

    private void handleUnknownType(SMS.Response.List.Message message) {
        try {
            log.info("skip: {}", API.writeXml(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void handleIncoming(SMS.Response.List.Message message) {
        if (message.getPhone()
                   .equals(redirectTo)) {
            System.out.println("received");
            System.out.println(message.getContent());
            if ("2".equals(message.getContent())) {
                orderVolume();
            }
        } else {
            handleUnknownType(message);
        }
    }

    private void handleDataVolumeExceeded(SMS.Response.List.Message message) {
        if (message.getContent()
                   .contains("noch kein Upgrade buchen")) {
            log.info("no upgrade yet");
        } else {
            orderVolume();
        }
    }

    synchronized void orderVolume() {
        SMS.Actions.send("70997", "2");
        SMS.Actions.send(redirectTo, "extends data volume");
    }
}
