package de.lgohlke.hilink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.lgohlke.API;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingTest {
    @Test
    void map_error() throws JsonProcessingException {
        // language=XML
        var response = """
                <?xml version="1.0" encoding="UTF-8"?>
                <error>
                    <code>125003</code>
                    <message/>
                </error>
                """;

        var o = new XMLProcessor().readXml(response, API.Error.class);

        assertThat(o.code()).isEqualTo(125003);
        assertThat(o.message()).isEmpty();
    }

    @Test
    void should_map_empty_list() throws JsonProcessingException {
        var xml = """
                <response>
                    <Messages/>
                </response>
                """;
        new XMLProcessor().readXml(xml, Response.class);
    }

    static class Response {
        @JacksonXmlElementWrapper(localName = "Messages")
        @JacksonXmlProperty(localName = "Message")
        List<String> messages;

    }
}
