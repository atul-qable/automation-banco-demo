package bradesco.custodia.bc2.framework.runner;

import bradesco.custodia.bc2.framework.readers.CommandlineOption;
import bradesco.custodia.bc2.framework.readers.JsonFileReader;
import com.beust.jcommander.JCommander;
//import io.tesbo.report.ReportGenerator;
import io.tesbo.report.ReportGenerator;
import org.json.JSONArray;
import org.testng.TestNG;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static String currentConfig = "";
    public static String pathForConfigurationFile = "";
    public String addCommandLineForConfig(String[] args, int index) {
        CommandlineOption option = null;
        String commandName = null;
        try {
            if(index == 0){
                option = new CommandlineOption();
                JCommander runOption = JCommander.newBuilder()
                        .addObject(option)
                        .build();
                runOption.parse(args);
                commandName = option.getConfigName();
            }
            else if(index == 1){
                option = new CommandlineOption();
                JCommander runOption = JCommander.newBuilder()
                        .addObject(option)
                        .build();
                runOption.parse(args);
                commandName = option.getPathForConfigurationFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commandName;
    }

    public void start(String[] args) {

        TestNG testng = new TestNG();
        JsonFileReader config = new JsonFileReader();
        try {
            pathForConfigurationFile = addCommandLineForConfig(args, 1);
            String configToRun = addCommandLineForConfig(args, 0);
            String pathSeparator = FileSystems.getDefault().getSeparator();
            currentConfig = config.getRunConfig();


//            if (currentConfig.equals(null)) {
//                currentConfig = config.getRunConfig();
//            }else {
//                currentConfig = configToRun;
//            }

            System.out.println("config to run : " + currentConfig);
            JSONArray suiteList = null;
            String directory_path = "." + pathSeparator + "src" + pathSeparator + "test" + pathSeparator + "resources" + pathSeparator + "suites" + pathSeparator;

            suiteList = config.getSuites(currentConfig);

            if (suiteList.length() != 0) {
                List<String> testFilesList = new ArrayList<String>();

                for (Object suiteName : suiteList) {
                    testFilesList.add(new File((directory_path + suiteName)).getAbsolutePath());
                }
                testng.setTestSuites(testFilesList);//you can add multiple suites either here by adding multiple files or include all suites needed in the testng.xml file
                testng.setOutputDirectory(TestRunner.pathForConfigurationFile+"/test-output");
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            testng.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ReportGenerator generator = new ReportGenerator();
            generator.generateTestNGReportDirectly(config.getReportKey(currentConfig), config.getCurrentReportDirectory(), config.getPlatform(currentConfig), config.getBrowser(currentConfig), "", "");
        }catch (Exception e)
        {
        }

    }
}
