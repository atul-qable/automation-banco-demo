package bradesco.custodia.bc2.performaction.autoweb;

import bradesco.custodia.bc2.framework.readers.JsonFileReader;
import bradesco.custodia.bc2.framework.runner.TestRunner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocatorReader {

    public static void main(String[] args) {
        LocatorReader reader = new LocatorReader();
        try {
            // System.out.println(reader.getLocatorValue("email_text_box"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getTestConfig() {
        String data = "";
        try {
            data = new String(Files.readAllBytes(Paths.get("src/config/TestConfig.json").toAbsolutePath()));
        } catch (IOException e) {
            System.out.println("Config file not found.");
        }
        JSONObject testConfig = new JSONObject();
        return testConfig;
    }

    public String getRunConfig() {
        JSONObject object = getTestConfig();
        return (String) object.get("run");
    }

    public JSONObject getConfigObject(String configName) {
        JSONObject object = getTestConfig();
        return (JSONObject) ((JSONObject) object.get("config")).get(configName);
    }

    public Map<String, String> getLocatorValue(String locatorName) {
        LocatorReader reader = new LocatorReader();
        JsonFileReader configReader = new JsonFileReader();

        JSONObject object = null;
        Map<String, String> locatorDetails = new HashMap<>();
        String platform = configReader.getPlatform(TestRunner.currentConfig);
        try {
            object = reader.getLocatorObject(locatorName, platform);
        } catch (Exception e) {
            e.printStackTrace();
        }
        locatorDetails.put("locatorType", object.get("locatorType").toString());
        if (platform.equalsIgnoreCase("web")) {
            locatorDetails.put("locatorValue", object.get("webLocator").toString());
        } else if (platform.equalsIgnoreCase("android")) {
            locatorDetails.put("locatorValue", object.get("androidLocator").toString());
        } else if (platform.equalsIgnoreCase("ios")) {
            locatorDetails.put("locatorValue", object.get("iosLocator").toString());
        }
        return locatorDetails;
    }

    public List<String> lookForLocatorJsonFile(String folderPath) {
        File dir = new File(folderPath);

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".json");
            }
        };

        List<String> result = null;
        try (Stream<Path> walk = Files.walk(Paths.get(folderPath))) {
            // We want to find only regular files
            result = walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".json"))
                    .map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject getLocatorObject(String locatorObjectName, String platform) throws Exception {
        File file = null;
        if ("web".equals(platform)) {
            file = new File("src/test/java/web/objectRepository/");

        } else if ("android".equalsIgnoreCase(platform) || "iOS".equalsIgnoreCase(platform)) {
            file = new File("src/test/java/mobile/objectRepository/");
        }

        JSONObject object = null;
        try {
            List<String> files = lookForLocatorJsonFile(file.getAbsolutePath());
            for (String filePath : files) {
                object = readLocatorFileAndGetObject(filePath, locatorObjectName);
                if (object != null) {
                    break;
                }
            }
        } catch (Exception e) {
            // Error while reading the directory
            e.printStackTrace();
        }

        if (object == null) {
            throw new Exception("Locator value " + locatorObjectName + " is not found in JSON file");
        }

        return object;
    }


    public JSONObject readLocatorFileAndGetObject(String filePath, String locatorObjectName) {
        LocatorReader reader = new LocatorReader();
        JSONObject object = null;
        JSONParser parser = new JSONParser();
        JSONObject json = null;

        try {
            json = (JSONObject) parser.parse(new String(Files.readAllBytes(Paths.get(filePath).toAbsolutePath())));
            object = (JSONObject) json.get(locatorObjectName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

}
