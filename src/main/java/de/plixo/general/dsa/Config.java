package de.plixo.general.dsa;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class Config {
    public int version;
    public @Nullable String profile;
    public @Nullable LinkedHashMap<String, String> layout;
    public @Nullable LinkedHashMap<String, String> uniform;
    public @Nullable ShaderConfig vertex;
    public @Nullable ShaderConfig fragment;
    @Override
    public String toString() {
        return "Config{" +
                "version=" + version +
                ", profile='" + profile + '\'' +
                ", layout=" + layout +
                ", uniform=" + uniform +
                ", vertex=" + vertex +
                ", fragment=" + fragment +
                '}';
    }
}
