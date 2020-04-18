package zday.kds.replay.demo.events;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;
import software.amazon.awssdk.core.SdkBytes;


public class Event {
  public static final String TYPE_FIELD = "Type";

  public final String payload;

  public Event(String payload) {
    if (!payload.endsWith("\n")) {
      //append a newline to output to make it easier digestible by firehose and athena
      this.payload = payload + "\n";
    } else {
      this.payload = payload;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(payload);
  }

  @Override
  public String toString() {
    return payload;
  }

  public SdkBytes toSdkBytes() {
    return SdkBytes.fromString(payload, Charset.forName("UTF-8"));
  }

  public ByteBuffer toByteBuffer() {
    return ByteBuffer.wrap(payload.getBytes(Charset.forName("UTF-8")));
  }
}
