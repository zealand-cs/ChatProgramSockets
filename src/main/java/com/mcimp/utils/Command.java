package com.mcimp.utils;

public enum Command {

    UPLOAD,
    DOWNLOAD;

    public int toInt() {
        switch (this) {
            case UPLOAD:
                return 1;
            case DOWNLOAD:
                return 2;
            default:
                throw new RuntimeException("unreachable when all branches are checked");
        }

    }

    public static Command fromInt(int c) {
        return switch (c) {
            case 1 -> Command.DOWNLOAD;
            case 2 -> Command.UPLOAD;
            default -> throw new RuntimeException("invalid command int");
        };
    }
}
