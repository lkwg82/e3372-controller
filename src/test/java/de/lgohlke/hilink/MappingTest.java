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
    void map_error() throws JsonProcessingException, APIErrorException {
        // language=XML
        var response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<error>\n" +
                "    <code>125003</code>\n" +
                "    <message/>\n" +
                "</error>\n";

        var o = new XMLProcessor().readXml(response, API.Error.class);

        assertThat(o.getCode()).isEqualTo(125003);
        assertThat(o.getMessage()).isEmpty();
    }

    @Test
    void should_map_empty_list() throws JsonProcessingException, APIErrorException {
        var xml = "<response>\n" +
                "    <Messages/>\n" +
                "</response>\n";
        new XMLProcessor().readXml(xml, Response.class);
    }

    @Test
    void map_session_token() throws APIErrorException, JsonProcessingException {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><SesInfo>947p2llduTUXKWm2f7sW3OHIRQ2r08QcDL2BQt1OP8qGuk5T5vwQZDizWkAJ0ki6mv2ioVxsKXwhla73ehgd9OFk2gehdP9hcY6EVlI8DFCAk0PoIyjxL4GRBh6h2rQm</SesInfo><TokInfo>T8lgAWOEnf0QBe4eVjRyi1wp9z2FQgl8</TokInfo></response>";

        new XMLProcessor().readXml(xml, API.SessionToken.class);
    }

    static class Response {
        @JacksonXmlElementWrapper(localName = "Messages")
        @JacksonXmlProperty(localName = "Message")
        List<String> messages;
    }
}
