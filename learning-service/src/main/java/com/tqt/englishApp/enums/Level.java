package com.tqt.englishApp.enums;

public enum Level {
    A1,
    A2,
    B1,
    B2,
    C1,
    C2;

    public Level getOffsetLevel(int offset) {
        int newIndex = this.ordinal() + offset;
        if (newIndex < 0)
            return values()[0];
        if (newIndex >= values().length)
            return values()[values().length - 1];
        return values()[newIndex];
    }
}
