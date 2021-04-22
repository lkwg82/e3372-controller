package de.lgohlke.hilink;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.xmlunit.assertj3.XmlAssert.assertThat;

public class XMLProcessorTest {
    private final XMLProcessor processor = new XMLProcessor();
    private final TestObject test = new TestObject("test", 1);

    @Test
    void should_write_xml() throws JsonProcessingException {

        var xml = processor.writeXml(test);

        assertThat(xml).valueByXPath("/TestObject/name").isEqualTo("test");
        assertThat(xml).valueByXPath("/TestObject/age").isEqualTo(1);
    }

    @Test
    void should_write_xml_with_indentation() throws JsonProcessingException {
        var xml = processor.writeXml(test);

        var expected = "<TestObject>\n" +
                "  <name>test</name>\n" +
                "  <age>1</age>\n" +
                "</TestObject>\n";
        assertThat(xml).isEqualTo(expected);
    }

    @Test
    void should_write_xml_without_indentation() throws JsonProcessingException {
        var xml = processor.writeXml(test, false);

        assertThat(xml).isEqualTo("<TestObject><name>test</name><age>1</age></TestObject>");
    }

    @Test
    void should_read_xml() throws JsonProcessingException, APIErrorException {
        var xml = "<TestObject>\n" +
                "  <name>test</name>\n" +
                "  <age>1</age>\n" +
                "</TestObject>\n";
        var actual = processor.readXml(xml, TestObject.class);

        assertThat(actual).isEqualTo(test);
    }

    @Test
    void should_fail_read_xml() {
        var xml = "<TestObject>\n" +
                "  <name>test</name>\n" +
                "  <age>1</age>\n" +
                "  <agex>1</agex>\n" +
                "</TestObject>\n";
        ThrowableAssert.ThrowingCallable callable = () -> processor.readXml(xml, TestObject.class);
        assertThatThrownBy(callable).isInstanceOf(JsonProcessingException.class);
    }

    @Test
    void should_map_to_error() {
        var xml = "<error>\n" +
                "  <code>1</code>\n" +
                "  <message/>\n" +
                "</error>\n";
        ThrowableAssert.ThrowingCallable callable = () -> processor.readXml(xml, TestObject.class);
        assertThatThrownBy(callable).isInstanceOf(APIErrorException.class);
    }

    @RequiredArgsConstructor
    @Getter
    static final class TestObject {
        private final String name;
        private final int age;
    }
}
