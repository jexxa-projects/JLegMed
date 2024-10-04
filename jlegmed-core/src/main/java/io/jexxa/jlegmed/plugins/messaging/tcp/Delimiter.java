package io.jexxa.jlegmed.plugins.messaging.tcp;

public enum Delimiter {
    CARRIAGE_RETURN("\r"),
    NEWLINE("\n"),
    BACKSPACE("\b"),
    FORM_FEED("\f"),
    CRRIAGE_RETURN_LINE_FEED("\r\n");


    private final String delimiterAsString;

    Delimiter(String delimiterAsString) {
        this.delimiterAsString = delimiterAsString;
    }

    public String asString() {
        return delimiterAsString;
    }
}
