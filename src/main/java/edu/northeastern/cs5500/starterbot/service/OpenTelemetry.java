package edu.northeastern.cs5500.starterbot.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;

public interface OpenTelemetry {
    Span span(String name);

    Span span(String name, SpanKind kind);
}
