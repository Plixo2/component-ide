package de.plixo.general;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Predicate;


public class FileUtil {

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


}
