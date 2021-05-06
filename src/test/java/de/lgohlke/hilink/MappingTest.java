package de.lgohlke.hilink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.lgohlke.hilink.api.Device;
import de.lgohlke.hilink.api.Error;
import de.lgohlke.hilink.api.Monitoring;
import de.lgohlke.hilink.api.SMS;
import de.lgohlke.hilink.api.SessionToken;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingTest {

    private final XMLProcessor xmlProcessor = new XMLProcessor();

    @Test
    void map_error() throws JsonProcessingException, APIErrorException {
        // language=XML
        var response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<error>\n" +
                "    <code>125003</code>\n" +
                "    <message/>\n" +
                "</error>\n";

        var o = xmlProcessor.readXml(response, Error.class);

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

        xmlProcessor.readXml(xml, SessionToken.class);
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

        xmlProcessor.readXml(xml, SMS.Response.List.class);
    }

    @Test
    @SneakyThrows
    void should_valid_xml_sms_list_request() {
        var request = new SMS.Request.List(SMS.BOXTYPE.INBOX);
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

    @Test
    @SneakyThrows
    void should_map_response_device_signal() {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "<pci>356</pci>\n" +
                "<sc></sc>\n" +
                "<cell_id>2576387</cell_id>\n" +
                "<rsrq>-7.0dB</rsrq>\n" +
                "<rsrp>-89dBm</rsrp>\n" +
                "<rssi>-69dBm</rssi>\n" +
                "<sinr>6dB</sinr>\n" +
                "<rscp></rscp>\n" +
                "<ecio></ecio>\n" +
                "<mode>7</mode>\n" +
                "<ulbandwidth>10MHz</ulbandwidth>\n" +
                "<dlbandwidth>10MHz</dlbandwidth>\n" +
                "<txpower>PPusch:6dBm PPucch:-5dBm PSrs:23dBm PPrach:7dBm</txpower>\n" +
                "<tdd>SubframeAssign:0 SubframePatterns:0 </tdd>\n" +
                "<ul_mcs>mcsUpCarrier1:29</ul_mcs>\n" +
                "<dl_mcs>mcsDownCarrier1Code0:0 mcsDownCarrier1Code1:0</dl_mcs>\n" +
                "<earfcn>DL:6300 UL:24300</earfcn>\n" +
                "<rrc_status></rrc_status>\n" +
                "<rac></rac>\n" +
                "<lac></lac>\n" +
                "<tac>49106</tac>\n" +
                "<band>20</band>\n" +
                "<nei_cellid></nei_cellid>\n" +
                "<plmn>26202</plmn>\n" +
                "<ims>0</ims>\n" +
                "<wdlfreq></wdlfreq>\n" +
                "<lteulfreq>8470</lteulfreq>\n" +
                "<ltedlfreq>8060</ltedlfreq>\n" +
                "<transmode>TM[3]</transmode>\n" +
                "<enodeb_id>0010064</enodeb_id>\n" +
                "<cqi0>9</cqi0>\n" +
                "<cqi1>9</cqi1>\n" +
                "<ulfrequency>847000kHz</ulfrequency>\n" +
                "<dlfrequency>806000kHz</dlfrequency>\n" +
                "<arfcn></arfcn>\n" +
                "<bsic></bsic>\n" +
                "<rxlev></rxlev>\n" +
                "</response>";

        var signal = xmlProcessor.readXml(xml, Device.Response.Signal.class);

        assertThat(signal.getPci()).isEqualTo(356);
        assertThat(signal.getRsrp()).isEqualTo(-89);
        assertThat(signal.getRssi()).isEqualTo(-69);
        assertThat(signal.getRsrq()).isEqualTo(-7);
    }

    @Test
    @SneakyThrows
    void should_map_response_monitoring_status() {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "<ConnectionStatus>901</ConnectionStatus>\n" +
                "<WifiConnectionStatus></WifiConnectionStatus>\n" +
                "<SignalStrength></SignalStrength>\n" +
                "<SignalIcon>2</SignalIcon>\n" +
                "<CurrentNetworkType>19</CurrentNetworkType>\n" +
                "<CurrentServiceDomain>3</CurrentServiceDomain>\n" +
                "<RoamingStatus>0</RoamingStatus>\n" +
                "<BatteryStatus></BatteryStatus>\n" +
                "<BatteryLevel></BatteryLevel>\n" +
                "<BatteryPercent></BatteryPercent>\n" +
                "<simlockStatus>0</simlockStatus>\n" +
                "<PrimaryDns>139.7.30.125</PrimaryDns>\n" +
                "<SecondaryDns>139.7.30.126</SecondaryDns>\n" +
                "<wififrequence>0</wififrequence>\n" +
                "<flymode>0</flymode>\n" +
                "<PrimaryIPv6Dns>2a01:860:0:300::153</PrimaryIPv6Dns>\n" +
                "<SecondaryIPv6Dns>2a01:860:0:300::53</SecondaryIPv6Dns>\n" +
                "<CurrentWifiUser></CurrentWifiUser>\n" +
                "<TotalWifiUser></TotalWifiUser>\n" +
                "<currenttotalwifiuser>0</currenttotalwifiuser>\n" +
                "<ServiceStatus>2</ServiceStatus>\n" +
                "<SimStatus>1</SimStatus>\n" +
                "<WifiStatus></WifiStatus>\n" +
                "<CurrentNetworkTypeEx>101</CurrentNetworkTypeEx>\n" +
                "<maxsignal>5</maxsignal>\n" +
                "<wifiindooronly>0</wifiindooronly>\n" +
                "<classify>hilink</classify>\n" +
                "<usbup>0</usbup>\n" +
                "<wifiswitchstatus>0</wifiswitchstatus>\n" +
                "<WifiStatusExCustom>0</WifiStatusExCustom>\n" +
                "<hvdcp_online>0</hvdcp_online>\n" +
                "<speedLimitStatus>0</speedLimitStatus>\n" +
                "<poorSignalStatus>0</poorSignalStatus>\n" +
                "</response>";

        var status = xmlProcessor.readXml(xml, Monitoring.Response.Status.class);

        assertThat(status.getSignalIcon()).isEqualTo(2);
    }

    @Test
    @SneakyThrows
    void should_map_response_monitoring_trafficStatistics() {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "<CurrentConnectTime>35856</CurrentConnectTime>\n" +
                "<CurrentUpload>679323450</CurrentUpload>\n" +
                "<CurrentDownload>1689913245</CurrentDownload>\n" +
                "<CurrentDownloadRate>0</CurrentDownloadRate>\n" +
                "<CurrentUploadRate>0</CurrentUploadRate>\n" +
                "<TotalUpload>6352863475</TotalUpload>\n" +
                "<TotalDownload>52302611857</TotalDownload>\n" +
                "<TotalConnectTime>912333</TotalConnectTime>\n" +
                "<showtraffic>1</showtraffic>\n" +
                "<MaxUploadRate>1086461</MaxUploadRate>\n" +
                "<MaxDownloadRate>1917665</MaxDownloadRate>\n" +
                "</response>";

        var status = xmlProcessor.readXml(xml, Monitoring.Response.TrafficStatistics.class);

        assertThat(status.getTotalDownload()).isEqualTo(52302611857L);
    }

    static class Response {
        @JacksonXmlElementWrapper(localName = "Messages")
        @JacksonXmlProperty(localName = "Message")
        List<String> messages;
    }
}
