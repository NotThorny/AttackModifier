package thorny.grasscutters.AttackModifier.objects;

import com.google.gson.Gson;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * A data container for the plugin configuration.
 * 
 * This class is used in conjunction with {@link Gson#toJson(Object)} and {@link Gson#fromJson(Reader, Type)}.
 * With {@link Gson}, it is possible to save and load configuration values from a JSON file.
 * 
 * You can set property defaults using `public Object property = (default value);`.
 * Use {@link Gson#fromJson(Reader, Type)} to load the values set from a reader/string into a new instance of this class.
 */
public final class PluginConfig {
    public boolean sendJoinMessage = true;
    public String joinMessage = "Welcome to the server!";

    /**
     * When saved with {@link Gson#toJson(Object)}, it produces:
     * {
     *     "sendJoinMessage": true,
     *     "joinMessage": "Welcome to the server!"
     * }
     */
}
