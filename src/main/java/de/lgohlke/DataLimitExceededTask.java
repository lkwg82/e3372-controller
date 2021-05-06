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

    @SneakyThrows
    @Override
    public void doTask() {

        log.info(" checking  ...");
        var redirectTo = System.getenv("REDIRECT_PHONE_NUMBER");
        Objects.requireNonNull(redirectTo, "missing number to redirect: REDIRECT_PHONE_NUMBER");
        try {
            SMS.Actions.list(SMS.BOXTYPE.INBOX)
                       .forEach(message -> {
                           log.info(message.toString());

                           handleSMS(redirectTo, message);

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

    private void handleSMS(String redirectTo, SMS.Response.List.Message message) {
        if (message.getType() == SMS.Response.List.Message.TYPE.DATA_VOLUME_EXCEEDED || message
                .getType() == SMS.Response.List.Message.TYPE.INCOMING) {
            var content = "received from " + message.getPhone() + "\n" +
//                        "at " + message.getDate() + "\n" +
                    message.getContent();
            SMS.Actions.send(redirectTo, content);
            if (message.getType() == SMS.Response.List.Message.TYPE.DATA_VOLUME_EXCEEDED) {
                if (message.getContent()
                           .contains("noch kein Upgrade buchen")) {
                    log.info("no upgrade yet");
                } else {
                    SMS.Actions.send(message.getPhone(), "2");
                    SMS.Actions.send(redirectTo, "extends data volume");
                }
            }
        } else {
            try {
                log.info("skip: {}", API.writeXml(message));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
