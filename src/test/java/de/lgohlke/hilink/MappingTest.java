package de.lgohlke.hilink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.lgohlke.API;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingTest {

    private XMLProcessor xmlProcessor = new XMLProcessor();

    @Test
    void map_error() throws JsonProcessingException, APIErrorException {
        // language=XML
        var response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<error>\n" +
                "    <code>125003</code>\n" +
                "    <message/>\n" +
                "</error>\n";

        var o = xmlProcessor.readXml(response, API.Error.class);

        assertThat(o.getCode()).isEqualTo(125003);
        assertThat(o.getMessage()).isEmpty();
    }

    @Test
    void should_map_empty_list() throws JsonProcessingException, APIErrorException {
        var xml = "<response>\n" +
                "    <Messages/>\n" +
                "</response>\n";
        xmlProcessor.readXml(xml, Response.class);
    }

    @Test
    void map_session_token() throws APIErrorException, JsonProcessingException {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><SesInfo>947p2llduTUXKWm2f7sW3OHIRQ2r08QcDL2BQt1OP8qGuk5T5vwQZDizWkAJ0ki6mv2ioVxsKXwhla73ehgd9OFk2gehdP9hcY6EVlI8DFCAk0PoIyjxL4GRBh6h2rQm</SesInfo><TokInfo>T8lgAWOEnf0QBe4eVjRyi1wp9z2FQgl8</TokInfo></response>";

        xmlProcessor.readXml(xml, API.SessionToken.class);
    }

    @Test
    void should_map_messages() throws APIErrorException, JsonProcessingException {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "<Count>8</Count>\n" +
                "<Messages>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40028</Index>\n" +
                "                        <Phone>01734982893</Phone>\n" +
                "                        <Content>Vodafone Mailbox: Der Anrufer hat keine Nachricht hinterlassen:\n" +
                "+491734982893\n" +
                " 23.04.2021 14:17:34\n" +
                " 1 Versuch\n" +
                "</Content>\n" +
                "                        <Date>2021-04-23 14:17:33</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>1</SmsType>\n" +
                "                </Message>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40031</Index>\n" +
                "                        <Phone>+491734982893</Phone>\n" +
                "                        <Content>fhf</Content>\n" +
                "                        <Date>2021-04-23 14:24:04</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>1</SmsType>\n" +
                "                </Message>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40030</Index>\n" +
                "                        <Phone>+491734982893</Phone>\n" +
                "                        <Content>huhu</Content>\n" +
                "                        <Date>2021-04-23 14:23:55</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>1</SmsType>\n" +
                "                </Message>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40029</Index>\n" +
                "                        <Phone>+491734982893</Phone>\n" +
                "                        <Content>huhu</Content>\n" +
                "                        <Date>2021-04-23 14:17:48</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>1</SmsType>\n" +
                "                </Message>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40034</Index>\n" +
                "                        <Phone>+4915206834192</Phone>\n" +
                "                        <Content></Content>\n" +
                "                        <Date>2021-04-23 18:22:36</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>5</SmsType>\n" +
                "                </Message>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40033</Index>\n" +
                "                        <Phone>+4915206834192</Phone>\n" +
                "                        <Content></Content>\n" +
                "                        <Date>2021-04-23 17:12:32</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>5</SmsType>\n" +
                "                </Message>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40032</Index>\n" +
                "                        <Phone>+4915206834192</Phone>\n" +
                "                        <Content></Content>\n" +
                "                        <Date>2021-04-23 16:31:42</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>5</SmsType>\n" +
                "                </Message>\n" +
                "                <Message>\n" +
                "                        <Smstat>0</Smstat>\n" +
                "                        <Index>40019</Index>\n" +
                "                        <Phone>+4915206834192</Phone>\n" +
                "                        <Content></Content>\n" +
                "                        <Date>2021-04-23 14:16:39</Date>\n" +
                "                        <Sca></Sca>\n" +
                "                        <SaveType>0</SaveType>\n" +
                "                        <Priority>0</Priority>\n" +
                "                        <SmsType>5</SmsType>\n" +
                "                </Message>\n" +
                "        </Messages>\n" +
                "</response>";

        xmlProcessor.readXml(xml, API.SMS.Response.List.class);
    }

    @Test
    @SneakyThrows
    void should_valid_xml_sms_list_request() {
        var request = new API.SMS.Request.List(API.SMS.BOXTYPE.INBOX);
        var xml = xmlProcessor.writeXml(request);

        var expected = "<request>\n" +
                "  <PageIndex>1</PageIndex>\n" +
                "  <ReadCount>1</ReadCount>\n" +
                "  <BoxType>1</BoxType>\n" +
                "  <SortType>1</SortType>\n" +
                "  <Ascending>0</Ascending>\n" +
                "  <UnreadPreferred>1</UnreadPreferred>\n" +
                "</request>\n" +
                "";
        assertThat(xml).isEqualTo(expected);
    }

    static class Response {
        @JacksonXmlElementWrapper(localName = "Messages")
        @JacksonXmlProperty(localName = "Message")
        List<String> messages;
    }
}
