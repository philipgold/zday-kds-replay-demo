package zday.kds.replay.demo.events;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;


public class WatermarkEvent extends Event {

  private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();

  public WatermarkEvent(Instant watermark)  {
    super(
        new ObjectNode(JsonNodeFactory.instance)
            .put("watermark", formatter.format(watermark))
            .put(TYPE_FIELD, "watermark")
            .toString()
    );
  }
}
