package io.github.stereo528.mainmenuchanger;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.JsonElement;

import static com.google.gson.JsonParser.parseReader;
import static io.github.stereo528.mainmenuchanger.client.MainMenuChangerClient.LOGGER;

public class Config {
    public static final Path CFG_PATH = FabricLoader.getInstance().getConfigDir().resolve("MainMenuChanger.json");

    public static boolean SMALLER_SPLASH;
    public static boolean CHANGE_COPYRIGHT;
    public static boolean CHANGE_VERSION;
    public static boolean MOD_COUNT;
    public static boolean NO_REALMS;
    public static boolean NO_SIDE_BUTTONS;

    public static void init() throws IOException {
        read();
        if (!Files.exists(CFG_PATH)) {
            create();
        }
    }

    public static void read() {
        if(CFG_PATH.toFile().exists()) {
            try (final FileReader fileReader = new FileReader(CFG_PATH.toString())) {
                final JsonElement jsonElement = parseReader(fileReader);
                if (!jsonElement.isJsonObject()) {
                    getDefaults();
                }

                final JsonObject object = jsonElement.getAsJsonObject();

                SMALLER_SPLASH = readBoolValue(object, "smaller_splash", false);
                CHANGE_COPYRIGHT = readBoolValue(object, "change_copyright_to_(c)", false);
                CHANGE_VERSION = readBoolValue(object, "shorter_version_text", false);
                MOD_COUNT = readBoolValue(object, "mod_count", false);
                NO_REALMS = readBoolValue(object, "disable_realms_button_and_notifs", false);
                NO_SIDE_BUTTONS = readBoolValue(object, "disable_side_buttons",false);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void create() {
        try (
                final FileWriter fileWriter = new FileWriter(CFG_PATH.toString());
                final JsonWriter jsonWriter = new JsonWriter(fileWriter);
        ) {
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject()
                .name("smaller_splash").value(false)
                .name("change_copyright_to_(c)").value(false)
                    .name("COMMENT_0").value("Changes the version text to <version> <modloader>")
                .name("shorter_version_text").value(false)
                .name("mod_count").value(false)
                .name("disable_realms_button_and_notifs").value(false)
                    .name("COMMENT_1").value("Removes Language and Accessibility buttons from the title screen")
                .name("disable_side_buttons").value(false)
            .endObject();

        } catch (IOException e) {
            LOGGER.error(String.valueOf(e));
        }
    }

    public static void getDefaults() {
        SMALLER_SPLASH = false;
        CHANGE_COPYRIGHT = false;
        CHANGE_VERSION = false;
        MOD_COUNT = false;
        NO_REALMS = false;
        NO_SIDE_BUTTONS = false;
    }

    public static boolean readBoolValue(JsonObject json, String key, boolean defaultValue) {
        final JsonElement jsonElement = json.get(key);
        if (jsonElement == null) {
            LOGGER.warn(key + " Is null! Please check your config! Returning Default Value!");
            return defaultValue;
        }
        try {
            return jsonElement.getAsBoolean();
        } catch (ClassCastException e) {
            LOGGER.warn(key + "Is not a Boolean! Returning Default Value!");
            return defaultValue;
        }
    }
}
