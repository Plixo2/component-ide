package de.plixo.general.dsa;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class ShaderConfig {
   public @Nullable LinkedHashMap<String, Object> constants;
   public @Nullable LinkedHashMap<String, String> functions;
   public @Nullable LinkedHashMap<String, String> output;
   public @Nullable String source;

    @Override
    public String toString() {
        return "ShaderConfig{" +
                "constants=" + constants +
                ", functions=" + functions +
                ", output=" + output +
                ", source='" + source + '\'' +
                '}';
    }
}
