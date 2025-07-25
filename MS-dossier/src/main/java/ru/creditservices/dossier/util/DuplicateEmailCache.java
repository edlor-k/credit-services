package ru.creditservices.dossier.util;

import ru.creditservices.dossier.model.enums.EmailTheme;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class DuplicateEmailCache {

    private static final long TTL_SECONDS = 300;
    private static final long CLEANUP_INTERVAL_SECONDS = 60;

    private static final Map<String, Instant> cache = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    static {
        cleaner.scheduleAtFixedRate(DuplicateEmailCache::cleanup, CLEANUP_INTERVAL_SECONDS,
                CLEANUP_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private DuplicateEmailCache() {}

    public static boolean isDuplicate(UUID statementId, EmailTheme theme) {
        String key = generateKey(statementId, theme);
        Instant now = Instant.now();

        Instant previous = cache.get(key);
        if (previous != null && now.isBefore(previous.plusSeconds(TTL_SECONDS))) {
            return true; // дубликат
        }

        cache.put(key, now);
        return false;
    }

    private static void cleanup() {
        Instant now = Instant.now();
        cache.entrySet().removeIf(entry -> now.isAfter(entry.getValue().plusSeconds(TTL_SECONDS)));
    }

    private static String generateKey(UUID statementId, EmailTheme theme) {
        return statementId.toString() + "::" + theme.name();
    }
}
