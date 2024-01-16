package edu.northeastern.cs5500.starterbot.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;

public class FakeOpenTelemetryService implements OpenTelemetry {

    @Override
    public Span span(String name) {
        return Span.current();
    }

    @Override
    public Span span(String name, SpanKind kind) {
        return Span.current();
    }
}
