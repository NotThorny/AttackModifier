package thorny.grasscutters.AttackModifier.utils;

import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

/*
 * A class that contains data for characters.
 * Character data is stored in a HashMap of String, CharacterAvatar,
 * where String is the name of the avatar, and the CharacterAvatar contains
 * the saved skill data.
 */
public final class Config {

    @SerializedName("characters")
    private HashMap<String, CharacterAvatar> characters;

    public HashMap<String, CharacterAvatar> getCharacters() {
        return this.characters;
    }

    public void setCharacters(HashMap<String, CharacterAvatar> a) {
        characters = a;
    }

    public String toCleanString() {
        return this.characters.toString();
    }

    public void setDefaults() {
        // Create a new character
        characters = new HashMap<>();

        // Create the "showcase" skills
        CharacterAvatar raiden = new CharacterAvatar(42906105, 42906108, 42906119);

        // Add to the list
        characters.put("shougun", raiden);
    }
}