package de.lgohlke.hilink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XMLProcessor {
    public <T> T readXml(String response, Class<T> clazz) throws JsonProcessingException {
        var mapper = new XmlMapper();

        try {
            return mapper.readValue(response, clazz);
        } catch (JsonProcessingException e) {
            System.err.println(response);
            throw e;
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
