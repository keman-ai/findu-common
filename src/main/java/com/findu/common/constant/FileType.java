package com.findu.common.constant;

public enum FileType {

    PROFILE_AVATAR("/profile/avatar"),

    PROFILE_BACKGROUND_IMAGE("/profile/background"),

    PROFILE_PAYMENT_QRCODE("/profile/payment"),

    PROFILE_PRESENTATION("/profile/presentation");

    private final String path;

    private FileType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
