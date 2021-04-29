package de.lgohlke.hilink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.lgohlke.API;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class XMLProcessor {
    public <T> T readXml(String response, Class<T> clazz) throws JsonProcessingException, APIErrorException {
        var mapper = new XmlMapper();

        try {
            return mapper.readValue(response, clazz);
        } catch (JsonProcessingException e) {
            log.warn(e);
            log.warn("response: {}", response);
            try {
                var error = mapper.readValue(response, API.Error.class);
                throw new APIErrorException(error);
            } catch (JsonProcessingException e2) {
                log.error(response);
                throw e2;
            }
        }
    }

    public String writeXml(Object o) throws JsonProcessingException {
        return writeXml(o, true);
    }

    public String writeXml(Object o, boolean indent) throws JsonProcessingException {
        var mapper = new XmlMapper();
        if (indent) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return mapper.writeValueAsString(o);
    }
}
