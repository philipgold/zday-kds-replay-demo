/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zday.kds.replay.demo.events;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class JsonEvent extends Event {
  public final Instant timestamp;
  public final Instant ingestionTime;

  public JsonEvent(String payload, Instant timestamp, Instant ingestionTime) {
    super(payload);

    this.timestamp = timestamp;
    this.ingestionTime = ingestionTime;
  }

  public static final Comparator<JsonEvent> timestampComparator =
      (JsonEvent o1, JsonEvent o2) -> o1.timestamp.compareTo(o2.timestamp);

  public static final Comparator<JsonEvent> ingestionTimeComparator =
      (JsonEvent o1, JsonEvent o2) -> o1.ingestionTime.compareTo(o2.ingestionTime);

  public static class Parser {
    private Instant ingestionStartTime = Instant.now();
    private Instant firstEventTimestamp;

    private final float speedupFactor;
    private final String timestampAttributeName;

    public Parser(float speedupFactor, String timestampAttributeName) {
      this.speedupFactor = speedupFactor;
      this.timestampAttributeName = timestampAttributeName;
    }

    public JsonEvent parse(String payload) {
      JsonNode json = Jackson.fromJsonString(payload, JsonNode.class);

      // adding json node type because there is no
      ((ObjectNode)json).put("Type", "news");

      Instant timestamp = Instant.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss zzz").parse(json.get(timestampAttributeName).asText()));


      if (firstEventTimestamp == null) {
        firstEventTimestamp = timestamp;
      }

      long deltaToFirstTimestamp = Math.round(Duration.between(firstEventTimestamp, timestamp).toMillis()/speedupFactor);

      Instant ingestionTime = ingestionStartTime.plusMillis(deltaToFirstTimestamp);

      return new JsonEvent(json.toString(), timestamp, ingestionTime);
    }

    public void reset() {
      firstEventTimestamp = null;
      ingestionStartTime = Instant.now();
    }
  }
}
