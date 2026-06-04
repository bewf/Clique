package me.bewf.clique.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.bewf.clique.CliqueMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class UpdateChecker {

    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Clique-UpdateChecker");
        t.setDaemon(true);
        return t;
    });

    private static boolean ran = false;
    private static boolean updateMessageSent = false;

    private UpdateChecker() {}

    public static boolean isUpdateMessageSent()        { return updateMessageSent; }
    public static void setUpdateMessageSent(boolean v) { updateMessageSent = v; }
    public static void resetRanFlag()                  { ran = false; }

    public static void checkOnce(String projectId, String projectSlug, String displayName,
                                 String currentVersion, String mcVersion, String loader) {
        if (ran) return;
        ran = true;

        if (projectId == null || projectId.isBlank()) {
            CliqueMod.LOGGER.info("[{}] Update check skipped — no project ID set", displayName);
            return;
        }

        EXEC.submit(() -> {
            try {
                String latest = fetchLatestVersion(projectId, mcVersion, loader);
                if (latest == null) {
                    CliqueMod.LOGGER.info("[{}] No release found for {} + {}", displayName, mcVersion, loader);
                    return;
                }
                if (!isNewer(latest, currentVersion)) {
                    CliqueMod.LOGGER.info("[{}] Up to date ({})", displayName, currentVersion);
                    return;
                }

                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null) return;
                    mc.player.sendMessage(buildMessage(projectSlug, displayName, latest, currentVersion), false);
                    updateMessageSent = true;
                });

                CliqueMod.LOGGER.info("[{}] Update available: {} → {}", displayName, currentVersion, latest);

            } catch (Throwable t) {
                CliqueMod.LOGGER.error("[{}] Update check failed: {}", displayName, t.toString());
            }
        });
    }

    private static String fetchLatestVersion(String projectId, String mcVersion, String loader) throws Exception {
        String apiUrl = "https://api.modrinth.com/v2/project/" + projectId + "/version"
                + "?limit=50"
                + "&game_versions=" + URLEncoder.encode("[\"" + mcVersion + "\"]", StandardCharsets.UTF_8)
                + "&loaders="       + URLEncoder.encode("[\"" + loader   + "\"]", StandardCharsets.UTF_8);

        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(6))
                .header("User-Agent", "Clique/" + CliqueMod.VERSION + " (+github.com/bewf/Clique)")
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            CliqueMod.LOGGER.warn("[Clique] Modrinth API returned HTTP {}", resp.statusCode());
            return null;
        }

        JsonElement root = JsonParser.parseString(resp.body());
        if (!root.isJsonArray()) return null;

        JsonArray arr = root.getAsJsonArray();
        if (arr.isEmpty()) return null;

        String best = null;
        int[] bestV = null;

        for (JsonElement el : arr) {
            if (!el.isJsonObject()) continue;
            JsonObject obj = el.getAsJsonObject();
            JsonElement vEl = obj.get("version_number");
            if (vEl == null) continue;
            String ver = vEl.getAsString();
            int[] pv = parseVersion(ver);
            if (best == null || compare(pv, bestV) > 0) {
                best  = ver;
                bestV = pv;
            }
        }
        return best;
    }

    private static boolean isNewer(String latest, String current) {
        return compare(parseVersion(latest), parseVersion(current)) > 0;
    }

    private static int compare(int[] a, int[] b) {
        for (int i = 0; i < 3; i++) {
            if (a[i] != b[i]) return Integer.compare(a[i], b[i]);
        }
        return 0;
    }

    private static int[] parseVersion(String v) {
        int[] out = {0, 0, 0};
        if (v == null) return out;
        String s = v.trim();
        int dash = s.indexOf('-');
        if (dash >= 0) s = s.substring(0, dash);
        String[] parts = s.split("\\.");
        for (int i = 0; i < out.length && i < parts.length; i++) {
            try {
                String n = parts[i].replaceAll("[^0-9]", "");
                out[i] = n.isEmpty() ? 0 : Integer.parseInt(n);
            } catch (Throwable ignored) {}
        }
        return out;
    }

    private static Text buildMessage(String slug, String displayName, String latest, String current) {
        String url = "https://modrinth.com/mod/" + slug + "/versions";

        MutableText link = Text.literal(" ▶ Click to download")
                .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)
                .styled(s -> s
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Open Modrinth versions page").formatted(Formatting.LIGHT_PURPLE)))
                );

        return Text.literal("[" + displayName + "] ").formatted(Formatting.AQUA)
                .append(Text.literal("Update available: ").formatted(Formatting.YELLOW))
                .append(Text.literal(latest).formatted(Formatting.GOLD))
                .append(Text.literal(" (you have " + current + ")").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(link);
    }
}