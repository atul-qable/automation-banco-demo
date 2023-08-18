package io.tesbo.report;

import com.beust.jcommander.JCommander;
import com.diogonunes.jcolor.Attribute;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.diogonunes.jcolor.Ansi.colorize;

public class ReportGenerator {
    ReportArgument argument = new ReportArgument();
    JCommander jc = new JCommander(argument);
    ReportBuilder builder = new ReportBuilder();
    RequestBuilder requestBuilder = new RequestBuilder();

    public void generatorReport(String key, String dirPath) {

        System.out.println(colorize("Creating a build", Attribute.BLUE_TEXT()));
        String buildKey = requestBuilder.createBuild(key);

        System.out.println(colorize("Fetching your test report..!!!", Attribute.BLUE_TEXT()));

        JSONObject a = builder.convertXmlToJSON(builder.readXmlFile(dirPath));

        System.out.println(colorize("Now Understanding it's meaning...", Attribute.BLUE_TEXT()));

        ReportDataConvertor convertor = new ReportDataConvertor(a);
        JSONObject report = convertor.PrepareFinalReport();

        System.out.println(colorize("Now Sending your test details to our haven..!!!", Attribute.BLUE_TEXT()));

        Boolean result = requestBuilder.updateResult(key, buildKey, report);
        if (!result) {
            System.out.println(colorize("Your Reports are reached the Tesbo World Now", Attribute.RED_TEXT()));
        } else {
            System.out.println(colorize("Something Wrong.!!! Test are not reached at Destination", Attribute.BLUE_TEXT()));
        }

    }

    //Used by StandAlon as Well as Tesbo Framework
    public void generateTestNGReportDirectly(String key, String dirPath, String platform, String browser, String browserVer, String platformVer) {

        System.out.println(colorize("Hey Let's send your test details to Tesbo World", Attribute.BLUE_TEXT()));

        System.out.println(colorize("Please Wait a Moment, We are checking details", Attribute.BLUE_TEXT()));

        System.out.println(colorize("Creating a build", Attribute.BLUE_TEXT()));
        String buildKey = requestBuilder.createBuild(key);

        System.out.println(colorize("Fetching your test report..!!!", Attribute.BLUE_TEXT()));

        JSONObject a = builder.convertXmlToJSON(builder.readXmlFile(dirPath));

        System.out.println(colorize("Now Understanding it's meaning...", Attribute.BLUE_TEXT()));
        ReportDataConvertor convertor = new ReportDataConvertor(a);
        convertor.batchModeReport(key, buildKey);
        System.out.println(colorize("Hureeeee!!!!.....All your Test Are Reached to it's Destination", Attribute.BLUE_TEXT()));

    }


    public void generateCucumberReportDirectly(String key, String dirPath, String platform, String browser, String browserVer, String platformVer) {
        System.out.println(colorize("Hey Let's send your test details to Tesbo World", Attribute.BLUE_TEXT()));

        System.out.println(colorize("Please Wait a Moment, We are checking details", Attribute.BLUE_TEXT()));

        System.out.println(colorize("Creating a build", Attribute.BLUE_TEXT()));
        String buildKey = requestBuilder.createBuild(key);

        JSONArray a = builder.readJsonFile(dirPath);

        CucumberDataConvertor dataConvertor = new CucumberDataConvertor(a);
        dataConvertor.batchModeReport(key, buildKey);
        System.out.println(colorize("Fetching your test report..!!!", Attribute.BLUE_TEXT()));

    }


    public static void main(String[] args) {
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.jc.parse(args);


        RequestBuilder builder1 = new RequestBuilder();
        builder1.getURL(reportGenerator.argument.env);

        if (reportGenerator.argument.framework.equalsIgnoreCase("cucumber")) {

            if (reportGenerator.argument.mode.equalsIgnoreCase("watcher")) {

                CucumberWatcher testngWatcher = new CucumberWatcher();

                System.out.println(colorize("Thank you for the Waking me up, I'm Tesbo Report", Attribute.BLUE_TEXT()));

                System.out.println(colorize("Now I will Keep Eye on Test Output Dir", Attribute.BLUE_TEXT()));

                while (true) {

                    if (testngWatcher.checkFileChanged(reportGenerator.argument.testDir)) {
                        System.out.println(colorize("ALERT....ALERT....I found a New Change,", Attribute.RED_TEXT()));
                        reportGenerator.generateCucumberReportDirectly(reportGenerator.argument.buildKey, reportGenerator.argument.testDir, reportGenerator.argument.platformName, reportGenerator.argument.browser, reportGenerator.argument.BrowserVersion, reportGenerator.argument.platformVer);
                        System.out.println(colorize("Back to Business now, Again Watching your report.", Attribute.BLUE_TEXT()));
                    }

                }

            } else {
                reportGenerator.generateCucumberReportDirectly(reportGenerator.argument.buildKey, reportGenerator.argument.testDir, reportGenerator.argument.platformName, reportGenerator.argument.browser, reportGenerator.argument.BrowserVersion, reportGenerator.argument.platformVer);
            }

        } else {

            if (reportGenerator.argument.mode.equalsIgnoreCase("watcher")) {

                TestngWatcher testngWatcher = new TestngWatcher();

                System.out.println(colorize("Thank you for the Waking me up, I'm Tesbo Report", Attribute.BLUE_TEXT()));

                System.out.println(colorize("Now I will Keep Eye on Test Output Dir", Attribute.BLUE_TEXT()));

                while (true) {

                    if (testngWatcher.checkFileChanged(reportGenerator.argument.testDir)) {
                        System.out.println(colorize("ALERT....ALERT....I found a New Change,", Attribute.RED_TEXT()));
                        reportGenerator.generateTestNGReportDirectly(reportGenerator.argument.buildKey, reportGenerator.argument.testDir, reportGenerator.argument.platformName, reportGenerator.argument.browser, reportGenerator.argument.BrowserVersion, reportGenerator.argument.platformVer);
                        System.out.println(colorize("Back to Business now, Again Watching your report.", Attribute.BLUE_TEXT()));
                    }

                }


            } else {
                reportGenerator.generateTestNGReportDirectly(reportGenerator.argument.buildKey, reportGenerator.argument.testDir, reportGenerator.argument.platformName, reportGenerator.argument.browser, reportGenerator.argument.BrowserVersion, reportGenerator.argument.platformVer);

            }

        }
    }

}
