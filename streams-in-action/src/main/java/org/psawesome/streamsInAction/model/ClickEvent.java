package org.psawesome.streamsInAction.model;

import java.time.Instant;
import java.util.Objects;


public class ClickEvent {

    private String symbol;
    private String link;
    private Instant timestamp;

    public ClickEvent(String symbol, String link, Instant timestamp) {
        this.symbol = symbol;
        this.link = link;
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getLink() {
        return link;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ClickEvent{" +
                "symbol='" + symbol + '\'' +
                ", link='" + link + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClickEvent)) return false;

        ClickEvent that = (ClickEvent) o;

        if (!Objects.equals(symbol, that.symbol)) return false;
        if (!Objects.equals(link, that.link)) return false;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        int result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
