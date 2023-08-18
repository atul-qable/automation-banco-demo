package io.tesbo.report;


import com.diogonunes.jcolor.Attribute;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.diogonunes.jcolor.Ansi.colorize;

public class CucumberDataConvertor {

    JSONArray reportData;

    public CucumberDataConvertor(JSONArray a) {
        this.reportData = a;
    }


    public void batchModeReport(String key, String buildKey) {

        JSONArray TestList = getAvailableTestList();

        int length = TestList.length();


        RequestBuilder requestBuilder = new RequestBuilder();

        System.out.println(colorize("Total " + length + " Test Found", Attribute.BLUE_TEXT()));
        System.out.println(colorize("Sending them to our heaven", Attribute.BLUE_TEXT()));

        for (int i = 0; i < TestList.length(); i++) {
            System.out.print(colorize(".", Attribute.MAGENTA_TEXT(), Attribute.BLUE_BACK()));
            if (i % 60 == 0) {
                System.out.println("");
            }

            JSONArray tempTestList = new JSONArray();
            tempTestList.put(TestList.get(i));
            JSONObject report = new JSONObject();
            JSONObject suite = new JSONObject();

            suite.put("started-at", ((JSONObject) TestList.get(i)).get("started-at"));
            suite.put("name", getSuiteName());
            suite.put("duration-ms", ((JSONObject) TestList.get(i)).get("duration-ms"));
            suite.put("tests", tempTestList);
            suite.put("finished-at", ((JSONObject) TestList.get(i)).get("finished-at"));
            report.put("Suite", suite);


            Boolean result = requestBuilder.updateResult(key, buildKey, report);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }

            if (!result) {
            } else {
                System.out.println(colorize("Something Wrong.!!! Test has missed the Train to Tesbo World", Attribute.RED_TEXT()));
            }
        }


    }


    public String getSuiteName() {
        net.minidev.json.JSONArray startedAt = JsonPath.parse(reportData.toString()).read("$.[*].id");

        return startedAt.get(0).toString();
    }

    public JSONArray getTestList() {

        JSONArray finalTestList = new JSONArray();

        for (int i = 0; i < reportData.length(); i++) {

            JSONObject obj = (JSONObject) reportData.get(i);

            JSONArray allTestList = obj.getJSONArray("elements");

            for (Object object : allTestList) {
                finalTestList.put(object);
            }


        }


        return finalTestList;
    }


    public JSONArray getAvailableTestList() {

        JSONArray finalTestList = new JSONArray();
        JSONArray getOldList = getTestList();

        for (Object test : getOldList) {

            JSONObject singleTestOb = getSingleTestObject(test);
            if (singleTestOb.length() >= 0) {
                finalTestList.put(singleTestOb);
            }
        }
        return finalTestList;

    }

    public JSONObject getSingleTestObject(Object testObject) {
        JSONObject object = new JSONObject();
        JSONObject singleTestObject = (JSONObject) testObject;

        if (singleTestObject.length() > 0) {

            JSONArray finalMethodList = new JSONArray();
            finalMethodList.put(createSingleMethodObject(singleTestObject));


            object.put("testID", UUID.randomUUID().toString());
            object.put("moduleName", getModuleName(reportData.toString()));
            object.put("final-test-status", getMethodStatus(singleTestObject.toString()));
            object.put("platformName", getPlatForm());
            object.put("platformVersion", getPlatVersion());
            object.put("browser", getBrowser());
            object.put("browserVersion", getBrowserVersion());
            object.put("deviceName", getDeviceName());
            object.put("started-at", getTestStartedAt(singleTestObject.toString()));
            object.put("finished-at", getTestFinishedAt(singleTestObject.toString()));
            object.put("duration-ms", getTestDuration(singleTestObject.toString()));
            object.put("name", getTestName(singleTestObject.toString()));


            object.put("failureMessage", "");
            object.put("full-stacktrace", getStackTrace(singleTestObject.toString()));


            object.put("methods", finalMethodList);

        }
        return object;
    }


    public String getModuleName(String object) {
        String folderName = "default";
        try {

            folderName = JsonPath.parse(object).read("$[0].id");

        } catch (Exception e) {


        }
        return folderName;
    }


    /**
     * @param object
     * @return final test result
     */
    public String getFinalTestResult(String object) {
        String finalTestResult = "SKIPPED";
        try {
            net.minidev.json.JSONArray list = JsonPath.parse(object).read("$.class.test-method[*].status");

            JSONArray testList = new JSONArray(list.toString());

            finalTestResult = "PASS";

            boolean isFailAvailable = false;
            for (Object singleMethodResult : testList) {

                if (singleMethodResult.toString().equalsIgnoreCase("FAIL")) {
                    isFailAvailable = true;
                }

            }

            if (isFailAvailable) {
                finalTestResult = "FAIL";
            }
        } catch (Exception e) {

        }
        return finalTestResult;
    }


    public String getTestStartedAt(String object) {
        String testStartedAt = JsonPath.parse(object).read("$.start_timestamp");
        return testStartedAt;
    }

    public String getTestFinishedAt(String object) {
        String testStartedAt = JsonPath.parse(object).read("$.start_timestamp");
        long totalTime = getTestDuration(object);
        ZonedDateTime start = ZonedDateTime.parse(testStartedAt);
        ZonedDateTime finish = start.plus(Duration.ofMillis(totalTime));
        return finish.toString();


    }

    public long getTestDuration(String object) {

        long totalDuration = 0;
        Boolean bool = false;

        try {
            net.minidev.json.JSONArray list = JsonPath.parse(object).read("$.steps[*].result.duration");
            JSONArray stepsResult = new JSONArray(list.toString());

            for (Object singleSteps : stepsResult) {

                long singleStepTime = Long.parseLong(singleSteps.toString());

                totalDuration = totalDuration + singleStepTime;
            }
        } catch (Exception e) {

        }

        long durationInMs = TimeUnit.NANOSECONDS.toMillis(totalDuration);
        return durationInMs;

    }

    public String getTestName(String object) {

        String testName = JsonPath.parse(object).read("$.name");
        return testName;
    }


    public String getStackTrace(String testObject) {

        String fullStackTrace = "Failed to get Stack Trace";

        try {
            if (getMethodStatus(testObject).equalsIgnoreCase("FAIL")) {

                net.minidev.json.JSONArray fullStackTraceArray = JsonPath.parse(testObject).read("$..error_message");
                fullStackTrace = fullStackTraceArray.get(0).toString();

            }
        } catch (Exception e) {

        }

        return fullStackTrace;
    }

    public String getScreenshot() {
        return "Android";
    }

    public JSONObject createSingleMethodObject(Object singleTestObject) {
        JSONObject methodObject = new JSONObject();

        methodObject.put("is-config", false);
        methodObject.put("name", getMethodName(singleTestObject.toString()));
        methodObject.put("status", getMethodStatus(singleTestObject.toString()));
        methodObject.put("started-at", getTestStartedAt(singleTestObject.toString()));
        methodObject.put("duration-ms", getTestDuration(singleTestObject.toString()));
        methodObject.put("finished-at", getTestFinishedAt(singleTestObject.toString()));
        methodObject.put("steps", getSteps(singleTestObject.toString()));
        methodObject.put("data-provider", false);


        return methodObject;
    }


    public JSONArray getSteps(String singleTestObject) {
        JSONArray stepArray = new JSONArray();
        try {
            net.minidev.json.JSONArray list = JsonPath.parse(singleTestObject).read("$.steps[*]");
            JSONArray intialStepList = new JSONArray(list.toString());

            for (Object singleSteps : intialStepList) {

                JSONObject step = new JSONObject();
                JSONObject singleStepObject = (JSONObject) singleSteps;
                String stepStatus = JsonPath.parse(singleSteps.toString()).read("$.result.status");
                String FinalStepStatus = "FAIL";
                if (stepStatus.equalsIgnoreCase("passed")) {
                    FinalStepStatus = "PASS";
                }
                if (stepStatus.equalsIgnoreCase("failed")) {
                    FinalStepStatus = "FAIL";
                }

                step.put("step", singleStepObject.get("keyword") + " " + singleStepObject.get("name"));
                step.put("status", FinalStepStatus);
                stepArray.put(step);

            }
        } catch (Exception e) {

        }

        return stepArray;
    }


    public String getMethodStatus(String methodObject) {
        String methodStatus = "PASS";
        Boolean bool = false;
        try {
            net.minidev.json.JSONArray list = JsonPath.parse(methodObject).read("$.steps[*].result.status");
            JSONArray stepsResult = new JSONArray(list.toString());

            for (Object singleSteps : stepsResult) {

                if (singleSteps.toString().equalsIgnoreCase("failed")) {
                    bool = true;
                    break;
                }

            }
        } catch (Exception e) {

        }

        if (bool) {
            methodStatus = "FAIL";
        }

        return methodStatus;
    }


    public boolean getIsConfig(String methodObject) {

        boolean bool = false;
        try {
            boolean isConfig = JsonPath.parse(methodObject).read("$.is-config");
            bool = isConfig;

        } catch (Exception e) {

        }
        return bool;
    }

    public boolean getDataProvider(String methodObject) {

        boolean bool = false;
        try {
            String isConfig = JsonPath.parse(methodObject).read("$.data-provider");
            bool = true;

        } catch (Exception e) {

        }
        return bool;
    }


    public String getMethodName(String methodObject) {


        String methodName = "steps";
        return methodName;
    }

    public String getPlatForm() {
        return ReportArgument.platformName;
    }

    public String getPlatVersion() {
        return ReportArgument.platformVer;
    }

    public String getBrowser() {
        return ReportArgument.browser;
    }

    public String getBrowserVersion() {
        return ReportArgument.BrowserVersion;
    }

    public String getDeviceName() {
        return "Mobile";
    }


}

