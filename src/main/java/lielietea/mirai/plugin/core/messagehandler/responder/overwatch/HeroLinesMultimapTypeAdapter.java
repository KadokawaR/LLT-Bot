package lielietea.mirai.plugin.core.messagehandler.responder.overwatch;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

class HeroLinesMultimapTypeAdapter<K, V> implements JsonSerializer<Multimap<K, V>>, JsonDeserializer<Multimap<K, V>> {

    @Override
    @SuppressWarnings("unchecked")
    public JsonElement serialize(Multimap<K, V> heroStringMultimap, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray object = new JsonArray();
        Map<K, Collection<V>> heroCollectionMap = heroStringMultimap.asMap();
        for (Hero hero : Hero.values()) {
            JsonObject jsonHeroLinesCollection = new JsonObject();
            JsonArray jsonHeroLines = new JsonArray();
            for (V v : heroCollectionMap.get((K) hero)) {
                jsonHeroLines.add((String) v);
            }
            jsonHeroLinesCollection.add(hero.name(), jsonHeroLines);
            object.add(jsonHeroLinesCollection);
        }
        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Multimap<K, V> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Multimap<K, V> heroStringMultimap = MultimapBuilder.hashKeys().arrayListValues().build();
        for (JsonElement element : jsonElement.getAsJsonArray()) {

            JsonObject jsonHeroPack = (JsonObject) element;
            Collection<String> heroLines = new ArrayList<>();
            Hero hero = Hero.valueOf((String) jsonHeroPack.keySet().toArray()[0]);

            for (JsonElement value : jsonHeroPack.get(hero.name()).getAsJsonArray()) {
                heroLines.add(value.getAsJsonPrimitive().getAsString());
            }

            for (String line : heroLines) {
                heroStringMultimap.put((K) hero, (V) line);
            }

        }

        return heroStringMultimap;
    }


}
