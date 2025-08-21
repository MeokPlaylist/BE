package com.meokplaylist.util;

import java.time.LocalDate;
import java.util.UUID;

public final class StorageKeyUtil {

    private StorageKeyUtil() {} // 인스턴스화 방지

    public static String buildKey(String topLevel, Long userId,Long feedId, String originalFilename) {
        String ext = getExtensionOrDefault(originalFilename, "bin");
        String uuid = UUID.randomUUID().toString();
        // 예: photos/123/uuid.png
        return String.format("%s/%d/%d/%s.%s", topLevel, userId,feedId ,uuid, ext);
    }

    private static String getExtensionOrDefault(String filename, String def) {
        if (filename == null) return def;
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return def;
        return filename.substring(idx + 1).toLowerCase();
    }
}
