package de.lgohlke.hilink;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.xmlunit.assertj3.XmlAssert.assertThat;

public class XMLProcessorTest {
    private final XMLProcessor processor = new XMLProcessor();

    @Test
    void should_write_xml() throws JsonProcessingException {
        var test = new TestObject("test", 1);

        var xml = processor.writeXml(test);

        assertThat(xml).valueByXPath("/TestObject/name").isEqualTo("test");
        assertThat(xml).valueByXPath("/TestObject/age").isEqualTo(1);
    }

    @Test
    void should_write_xml_with_indentation() throws JsonProcessingException {
        var test = new TestObject("test", 1);

        var xml = processor.writeXml(test);

        var expected = """
                <TestObject>
                  <name>test</name>
                  <age>1</age>
                </TestObject>
                """;
        assertThat(xml).isEqualTo(expected);
    }

    @Test
    void should_write_xml_without_indentation() throws JsonProcessingException {
        var test = new TestObject("test", 1);

        var xml = processor.writeXml(test, false);

        assertThat(xml).isEqualTo("<TestObject><name>test</name><age>1</age></TestObject>");
    }

    @Test
    void should_read_xml() throws JsonProcessingException {
        var test = new TestObject("test", 1);

        var xml = """
                <TestObject>
                  <name>test</name>
                  <age>1</age>
                </TestObject>
                """;
        var actual = processor.readXml(xml, TestObject.class);

        assertThat(actual).isEqualTo(test);
    }

    record TestObject(String name, int age) {

    }
}
