package de.plixo.general.dsa;


import com.moandjiezana.toml.Toml;
import de.plixo.general.Tuple;
import de.plixo.rendering.targets.Shader;
import lombok.val;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * java port
 * <a href="https://crates.io/crates/dsa-lib">...</a>
 */
public class Dsa {

    public static Config compileToConfig(String path) {
        final Class<Config> targetClass = Config.class;
        return new Toml().read(new File("content/shader/" + path)).to(targetClass);
    }

    public static Tuple<String, String> compileToSrc(String path) {
        final Class<Config> targetClass = Config.class;
        final Config config = new Toml().read(new File("content/shader/" + path)).to(targetClass);
        return new Tuple<>(compile_vertex(config), compile_fragment(config));
    }

    public static Tuple<Config, Shader> compileToShader(String path) {
        final Class<Config> targetClass = Config.class;
        final Config config = new Toml().read(new File("content/shader/" + path)).to(targetClass);
        final String vertex = compile_vertex(config);
        final String fragment = compile_fragment(config);
        final Shader shader = Shader.fromSource(vertex, fragment);
        return new Tuple<>(config, shader);
    }

    private static String compile_vertex(Config config) {
        val builder = new StringBuilder();
        builder.append(format("#version {} {}\n", config.version, config.profile));
        val index = new AtomicInteger();
        if (config.layout != null)
            config.layout.forEach((name, type) -> builder.append(
                    format("layout (location = {}) in {} {}; \n",
                            index.getAndIncrement(),
                            type,
                            name)));
        builder.append('\n');

        if (config.uniform != null)
            config.uniform.forEach((name, type) -> builder.append(format("uniform {} {}; \n", type, name)));
        builder.append('\n');
        if (config.vertex != null && config.vertex.output != null)
            config.vertex.output.forEach((name, type) -> builder.append(format("out {} {}; \n", type, name)));
        builder.append('\n');

        if (config.vertex != null && config.vertex.constants != null) {
            config.vertex.constants.forEach((name, value) -> {
                if (value != null) {
                    if (value instanceof String str) {
                        builder.append(format("#define {}{} \n", name, str));
                    } else {
                        builder.append(format("#define {} {} \n", name, value));
                    }
                }
            });
        }

        builder.append('\n');
        builder.append("void main() {\n \n");
        if (config.vertex != null)
            builder.append(config.vertex.source);
        builder.append("\n}\n");

        builder.append('\n');

        if (config.vertex != null && config.vertex.functions != null) {
            config.vertex.functions.forEach((key, value) -> builder.append(format("{} \n\n", value)));
        }
        return builder.toString();
    }



    private static String compile_fragment(Config config) {
        val builder = new StringBuilder();
        builder.append(format("#version {} {}\n", config.version, config.profile));
        builder.append('\n');

        if (config.uniform != null)
            config.uniform.forEach((name, type) -> builder.append(format("uniform {} {}; \n", type, name)));
        builder.append('\n');
        if (config.vertex != null && config.vertex.output != null)
            config.vertex.output.forEach((name, type) -> builder.append(format("in {} {}; \n", type, name)));
        builder.append('\n');
        if (config.fragment != null && config.fragment.output != null)
            config.fragment.output.forEach((name, type) -> builder.append(format("out {} {}; \n", type, name)));
        builder.append('\n');
        if (config.fragment != null && config.fragment.constants != null) {
            config.fragment.constants.forEach((name, value) -> {
                if (value != null) {
                    if (value instanceof String str) {
                        builder.append(format("#define {}{} \n", name, str));
                    } else {
                        builder.append(format("#define {} {} \n", name, value));
                    }
                }
            });
        }

        builder.append('\n');
        builder.append("void main() {\n \n");
        if (config.fragment != null)
            builder.append(config.fragment.source);
        builder.append("\n}\n");

        builder.append('\n');
        if (config.fragment != null && config.fragment.functions != null) {
            config.fragment.functions.forEach((key, value) -> builder.append(format("{} \n\n", value)));
        }

        return builder.toString();
    }

    private static String format(String str, Object... objects) {
        val builder = new StringBuilder();
        final String[] split = str.split("\\{}");

        var index = 0;
        int i;
        for (i = 0; i < split.length - 1; i++) {
            builder.append(split[i]);
            builder.append(objects[index++]);
        }
        builder.append(split[i]);

        return builder.toString();
    }
}
