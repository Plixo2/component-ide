package de.plixo.intermediate;

import com.google.common.reflect.ClassPath;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIChildEvent;
import de.plixo.intermediate.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TokenRegistry {
    static List<Class<? extends Token>> classes = new ArrayList<>();
    static List<Token> tokens = new ArrayList<>();

    @SubscribeEvent
    public static void ui(@NotNull UIChildEvent event) {
        try {
            ClassPath.from(TokenRegistry.class.getClassLoader()).getAllClasses().stream()
                    .filter(clazz -> clazz.getPackageName().equalsIgnoreCase("de.plixo.intermediate.tokens"))
                    .map(ClassPath.ClassInfo::load).collect(Collectors.toSet()).forEach(ref -> {
                        try {
                            if (!Modifier.isAbstract(ref.getModifiers())) {
                                classes.add((Class<? extends Token>) ref);
                                tokens.add((Token) ref.getConstructors()[0].newInstance());
                            }
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
