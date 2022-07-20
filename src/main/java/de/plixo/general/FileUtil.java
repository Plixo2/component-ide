package de.plixo.general;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import de.plixo.game.meta.MetaTest;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.plixo.gsonplus.GsonPlus;
import org.plixo.gsonplus.GsonPlusBuilder;
import org.plixo.gsonplus.GsonPlusConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Predicate;


public class FileUtil {
    static GsonPlusBuilder builder = new GsonPlusBuilder();
    static GsonPlus serializer = new GsonPlus();


    static {
        GsonPlusConfig.setOverwriteLists(true);
        GsonPlusConfig.setThrowObjectNullException(true);
        GsonPlusConfig.setUseDefaultCase(true);
        GsonPlusConfig.setClassLoader(FileUtil.class.getClassLoader());

        GsonPlusConfig.addAdapter(Color.class, () -> new Color(0));
        GsonPlusConfig.addAdapter(MetaTest.AbstractItem.class, () -> new MetaTest.AbstractItem(0,null));
        GsonPlusConfig.addAdapter(MetaTest.AbstractItem2.class, () -> new MetaTest.AbstractItem2(0,null));
        GsonPlusConfig.addPrimitive(Vector3f.class, new GsonPlusConfig.IObjectValue<Vector3f>() {
            @Override
            public Object toObject(JsonElement jsonElement) {
                val str = jsonElement.getAsString();
                assert str.startsWith("[");
                assert str.endsWith("]");
                val substring = str.substring(1, str.length() - 1);
                val splits = substring.split(",");
                assert splits.length == 3;
                val x = Float.parseFloat(splits[0]);
                val y = Float.parseFloat(splits[1]);
                val z = Float.parseFloat(splits[2]);
                return new Vector3f(x, y, z);
            }

            @Override
            public Vector3f getDefault() {
                return new Vector3f(0, 0, 0);
            }

            @Override
            public String toString(Vector3f vector3f) {
                return "[" + vector3f.x + "," + vector3f.y + "," + vector3f.z + "]";
            }
        });

    }

    static JsonParser jsonParser = new JsonParser();

    public static String STANDARD_PATH = "";


    public static ArrayList<String> loadFromFile(File file) {
        try {
            if (!file.exists()) {
                makeFile(file);
            }

            ArrayList<String> list = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                list.add(data);
            }
            scanner.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static JsonElement loadFromJson(File file) {
        try {
            if (!file.exists()) {
                makeFile(file);
                return null;
            }
            FileReader json = new FileReader(file);

            JsonElement parse = jsonParser.parse(json);
            json.close();
            return parse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String loadAsString(String path) {
        StringBuilder result = new StringBuilder();
        path = FileUtil.STANDARD_PATH + path;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't find the file at " + path);
        }

        return result.toString();
    }

    public static byte[] loadAsBytes(File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static boolean save(File file, String txt) {
        try {
            if (!file.exists()) {
                makeFile(file);
            }

            FileWriter fw = new FileWriter(file);
            fw.write(txt);

            fw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean save(File file, ArrayList<String> lines, boolean lineSeparator) {
        try {

            if (!file.exists()) {
                makeFile(file);
            }

            FileWriter fw = new FileWriter(file);
            for (String str : lines) {
                fw.write(str);
                if (lineSeparator)
                    fw.write(System.getProperty("line.separator"));
            }

            fw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveBytes(File file, byte[] bytes) {
        try {
            FileUtils.writeByteArrayToFile(file, bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveJsonObj(File file, JsonElement json) {

        try {
            if (!file.exists()) {
                makeFile(file);
            }
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent("    ");
            jsonWriter.setLenient(true);
            Streams.write(json, jsonWriter);
            String str = stringWriter.toString();
            FileWriter fw = new FileWriter(file);
            fw.write(str);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeFile(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void makeFolder(File file) {
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> listFolders(File file, Predicate<File> filter) {
        try {
            ArrayList<String> names = new ArrayList<>();

            if (!file.exists()) {
                try {
                    file.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return names;
            }

            File[] directories = file.listFiles(File::isDirectory);
            assert directories != null;
            for (File files : directories) {
                if (filter.test(files)) {
                    names.add(files.getName());
                }
            }
            return names;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getExtension(File file) {
        return FilenameUtils.getExtension(file.getAbsolutePath());
    }

    public static File getFileFromName(String name) {
        return new File(STANDARD_PATH + name);
    }

    public static File getFolderFromName(String name) {
        return new File("res/" + name);
    }


    public static <T> T loadObj(@NotNull T dummy, @NotNull File file) throws ReflectiveOperationException {
        val jsonElement = loadFromJson(file);
        return (T) builder.create(dummy, jsonElement);
    }

    public static JsonElement writeObj(@NotNull Object object) throws ReflectiveOperationException {
        return serializer.toJson(object);
    }

    public static void writeObj(@NotNull Object object, @NotNull File file) throws ReflectiveOperationException {
        saveJsonObj(file,serializer.toJson(object));
    }

}
