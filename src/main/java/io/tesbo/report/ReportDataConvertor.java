package io.tesbo.report;


import com.diogonunes.jcolor.Attribute;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.UUID;

import static com.diogonunes.jcolor.Ansi.colorize;

public class ReportDataConvertor {

    JSONObject reportData;

    ReportDataConvertor(JSONObject reportData) {
        this.reportData = reportData;
    }

    public JSONObject PrepareFinalReport() {
        JSONObject report = new JSONObject();
        JSONObject suite = new JSONObject();
        suite.put("started-at", getStartedAt());
        suite.put("name", getSuiteName());
        suite.put("finished-at", getFinishedAt());
        suite.put("duration-ms", getDuration());
        suite.put("tests", getAvailableTestList());
        report.put("Suite", suite);


        return report;
    }


    public void batchModeReport(String key, String buildKey) {

        JSONArray TestList = getAvailableTestList();
        /*
         * Getting the list of the methods from the test
         * */

        int length = TestList.length();
        System.out.println("TestLength" + length);
        JSONArray finalTestList = new JSONArray();

        for (int i = 0; i < length; i++) {

            try {
                JSONArray methodList = (JSONArray) ((JSONObject) TestList.get(i)).get("methods");

                for (Object a : methodList) {
                    JSONObject singleMethod = (JSONObject) a;
                    JSONArray tempArray = new JSONArray();
                    if ((boolean) singleMethod.get("is-config")) {
                    } else {
                        tempArray.put(singleMethod);
                        finalTestList.put(createTestFromMethodObject(TestList.get(i), tempArray));
                    }

                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }

        }


        RequestBuilder requestBuilder = new RequestBuilder();


        System.out.println(finalTestList.length());

        for (int i = 0; i < finalTestList.length(); i++) {
            System.out.print(colorize(".", Attribute.MAGENTA_TEXT(), Attribute.BLUE_BACK()));
            if (i % 60 == 0) {
                System.out.println("");
            }

            JSONArray tempTestList = new JSONArray();
            tempTestList.put(finalTestList.get(i));

            JSONObject report = new JSONObject();

            JSONObject suite = new JSONObject();

            suite.put("started-at", getStartedAt());
            suite.put("name", getSuiteName());

            suite.put("duration-ms", getDuration());
            suite.put("tests", tempTestList);
            suite.put("finished-at", getFinishedAt());
            report.put("Suite", suite);


            Boolean result = requestBuilder.updateResult(key, buildKey, report);

            if (!result) {
            } else {
                System.out.println(colorize("Something Wrong.!!! Test has missed the Train to Tesbo World", Attribute.RED_TEXT()));
            }
        }


        //close the build
        System.out.println("Closing the build");
        requestBuilder.closeBuild(key, buildKey);

    }


    public String getStartedAt() {
        String startedAt = JsonPath.parse(reportData.toString()).read("$.testng-results.suite.started-at");
        return startedAt;
    }

    public String getFinishedAt() {
        String startedAt = JsonPath.parse(reportData.toString()).read("$.testng-results.suite.finished-at");
        return startedAt;
    }

    public int getDuration() {
        int startedAt = JsonPath.parse(reportData.toString()).read("$.testng-results.suite.duration-ms");
        return startedAt;
    }

    public String getSuiteName() {
        String startedAt = JsonPath.parse(reportData.toString()).read("$.testng-results.suite.name");
        return startedAt;
    }

    public JSONArray getTestList() {

        JSONArray testList = null;
        try {
            LinkedHashMap list = JsonPath.parse(reportData.toString()).read("$.testng-results.suite.test");

            try {
                testList = new JSONArray(list);
            } catch (Exception e) {
                testList = new JSONArray();
                testList.put(new JSONObject(list).toString());
            }

        } catch (ClassCastException e) {
            net.minidev.json.JSONArray list = JsonPath.parse(reportData.toString()).read("$.testng-results.suite.test");

            try {
                testList = new JSONArray(list.toString());
            } catch (Exception e1) {
                testList = new JSONArray();
                testList.put(new JSONObject(list).toString());
            }

        }
        return testList;
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
        String singleTestObject = testObject.toString();
        JSONArray methods = getMethodArray(testObject.toString());

        if (methods.length() > 0) {
            object.put("testID", UUID.randomUUID().toString());
            object.put("moduleName", getModuleName(singleTestObject));
            object.put("final-test-status", getFinalTestResult(singleTestObject));
            object.put("platformName", getPlatForm());
            object.put("platformVersion", getPlatVersion());
            object.put("browser", getBrowser());
            object.put("browserVersion", getBrowserVersion());
            object.put("deviceName", getDeviceName());
            object.put("started-at", getTestStartedAt(singleTestObject));
            object.put("finished-at", getTestFinishedAt(singleTestObject));
            object.put("duration-ms", getTestDuration(singleTestObject));
            object.put("name", getTestName(singleTestObject));
            object.put("failureMessage", getFailureMessage(singleTestObject));
            object.put("full-stacktrace", getStacktraceForTestObject(singleTestObject));
            object.put("screenshot", getScreenshot());
            object.put("methods", methods);
        }
        return object;
    }

    /**
     * This method will create a separate test from the methods, this will be used when the data provider run the test
     *
     * @param testObject
     * @return
     */
    public JSONObject createTestFromMethodObject(Object testObject, JSONArray methods) {


        JSONObject object = new JSONObject();
        String singleTestObject = testObject.toString();
        object.put("testID", UUID.randomUUID().toString());
        object.put("moduleName", getModuleName(singleTestObject));
        object.put("final-test-status", getTestResultForSingleMethods(methods));
        object.put("platformName", getPlatForm());
        object.put("platformVersion", getPlatVersion());
        object.put("browser", getBrowser());
        object.put("browserVersion", getBrowserVersion());
        object.put("deviceName", getDeviceName());
        object.put("started-at", getTestStartedAt(singleTestObject));
        object.put("finished-at", getTestFinishedAt(singleTestObject));
        object.put("duration-ms", getTestDuration(singleTestObject));
        object.put("name", getTestName(singleTestObject));
        object.put("failureMessage", getFailureMessage(singleTestObject));

        object.put("full-stacktrace", getStackTrace(singleTestObject, "singleTest"));

        object.put("screenshot", getScreenshot());
        object.put("methods", methods);

        return object;
    }


    public String getModuleName(String object) {
        String folderName = "default";

        try {

            String ClassName = JsonPath.parse(object).read("$.class.name");
            String[] nameSplitList = ClassName.split("\\.");

            folderName = nameSplitList[nameSplitList.length - 2];
        } catch (Exception e) {

            try {
                folderName = JsonPath.parse(object).read("$.class.name");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return folderName;
    }


    public String getTestResultForSingleMethods(JSONArray array) {

        String finalTestResult = "SKIPPED";
        try {
            net.minidev.json.JSONArray list = JsonPath.parse(array.toString()).read("$.[*].status");

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

    public String getTestStartedAt(String object) {
        String testStartedAt = JsonPath.parse(object).read("$.started-at");

        return testStartedAt;
    }

    public String getTestFinishedAt(String object) {

        String testFinishedAt = JsonPath.parse(object).read("$.finished-at");


        return testFinishedAt;
    }

    public int getTestDuration(String object) {
        int testDuration = JsonPath.parse(object).read("$.duration-ms");
        return testDuration;
    }

    public String getTestName(String object) {

        String testName = JsonPath.parse(object).read("$.name");

        return testName;
    }


    public String getFailureMessage(String testObject) {

        String failureMessage = "Failed to get the filed message";

        try {
            if (getFinalTestResult(testObject).equalsIgnoreCase("FAIL")) {

                net.minidev.json.JSONArray list = JsonPath.parse(testObject).read("$.class.test-method");


                JSONArray methodList = new JSONArray(list);


                for (Object singleMethodResult : methodList) {
                    try {
                        failureMessage = JsonPath.parse(singleMethodResult.toString()).read("$.exception.message");

                        if (!failureMessage.equalsIgnoreCase("")) {
                            break;
                        }
                    } catch (Exception e) {

                    }
                }


            }
        } catch (Exception e) {

        }

        return failureMessage;
    }

    public String getStacktraceForTestObject(String testObject) {
        String fullStackTrace = "Failed to get Stack Trace";

        try {
            if (getFinalTestResult(testObject).equalsIgnoreCase("FAIL")) {
                net.minidev.json.JSONArray stack = JsonPath.parse(testObject.toString()).read("$.class.test-method[*].exception.full-stacktrace");

                JSONArray a = new JSONArray(stack.toString());
                fullStackTrace = (String) a.get(0);
            }
        } catch (Exception e) {

        }
        return fullStackTrace;
    }


    public String getStackTrace(String testObject, String from) {
        String fullStackTrace = "Failed to get Stack Trace";
        if (from.equalsIgnoreCase("singleMethods")) {
            fullStackTrace = JsonPath.parse(testObject.toString()).read("$.exception.full-stacktrace");

        } else if (from.equalsIgnoreCase("singleTest")) {
            fullStackTrace = JsonPath.parse(testObject.toString()).read("$.full-stacktrace");
        } else {


            try {
                if (getFinalTestResult(testObject).equalsIgnoreCase("FAIL")) {

                    net.minidev.json.JSONArray list = JsonPath.parse(testObject).read("$.class.test-method");

                    JSONArray methodList = new JSONArray(list);

                    for (Object singleMethodResult : methodList) {
                        try {
                            fullStackTrace = JsonPath.parse(singleMethodResult.toString()).read("$.exception.full-stacktrace");
                            if (!fullStackTrace.equalsIgnoreCase("Failed to get Stack Trace")) {
                                break;
                            }
                        } catch (Exception e) {

                            fullStackTrace = JsonPath.parse(singleMethodResult.toString()).read("$.full-stacktrace");
                            if (!fullStackTrace.equalsIgnoreCase("Failed to get Stack Trace")) {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                try {


                    net.minidev.json.JSONArray stack = JsonPath.parse(testObject.toString()).read("$.full-stacktrace");

                    JSONArray a = new JSONArray(stack.toString());
                    fullStackTrace = (String) a.get(0);
                } catch (Exception e2) {


                }

            }
        }
        return fullStackTrace;
    }

    public String getScreenshot() {
        return "";
    }


    public JSONArray getMethodArray(String singleTestObject) {
        JSONArray finalMethod = new JSONArray();

        try {
            net.minidev.json.JSONArray list = JsonPath.parse(singleTestObject).read("$.class.test-method");
            JSONArray intialMethodList = new JSONArray(list.toString());

            for (Object singleMethodObject : intialMethodList) {
                finalMethod.put(getSingleMethodObject(singleMethodObject));
            }
        } catch (Exception e) {

            try {

                LinkedHashMap list = JsonPath.parse(singleTestObject).read("$.class.test-method");

                try {
                    JSONArray intialMethodList = new JSONArray(list);

                    for (Object singleMethodObject : intialMethodList) {
                        finalMethod.put(getSingleMethodObject(singleMethodObject));
                    }
                } catch (Exception e2) {

                    JSONObject intialMethodObject = new JSONObject(list);

                    JSONArray intialMethodList = new JSONArray();
                    intialMethodList.put(intialMethodObject);

                    for (Object singleMethodObject : intialMethodList) {
                        finalMethod.put(getSingleMethodObject(singleMethodObject));
                    }

                }


            } catch (Exception methodnotAvailable) {

            }

        }

        return finalMethod;
    }


    public JSONObject getSingleMethodObject(Object singleMethodObject) {
        JSONObject methodObject = new JSONObject();

        try {
            methodObject.put("is-config", getIsConfig(singleMethodObject.toString()));
            methodObject.put("name", getMethodName(singleMethodObject.toString()));
            methodObject.put("status", getMethodStatus(singleMethodObject.toString()));
            methodObject.put("started-at", getMethodStartedAt(singleMethodObject.toString()));
            methodObject.put("duration-ms", getDurationAt(singleMethodObject.toString()));
            methodObject.put("finished-at", getMethodFinished(singleMethodObject.toString()));
            methodObject.put("steps", getSteps(singleMethodObject.toString()));
            methodObject.put("data-provider", getDataProvider(singleMethodObject.toString()));
            methodObject.put("full-stacktrace", getStackTrace(singleMethodObject.toString(), "singleMethods"));
        } catch (Exception singleMethodObject1) {
            singleMethodObject1.printStackTrace();
        }

        return methodObject;
    }


    public JSONArray getSteps(String methodObject) {
        JSONArray stepArray = new JSONArray();
        try {


            net.minidev.json.JSONArray list = JsonPath.parse(methodObject).read("$.reporter-output.line");

            JSONArray intialStepList = new JSONArray(list.toString());

            for (Object singleSteps : intialStepList) {

                JSONObject step = new JSONObject();
                step.put("step", singleSteps.toString());
                step.put("status", "PASS");
                stepArray.put(step);

            }
        } catch (Exception e) {
            JSONObject step = new JSONObject();
            step.put("step", "");
            step.put("status", "PASS");
            stepArray.put(step);

        }

        return stepArray;
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

        String methodName = JsonPath.parse(methodObject).read("$.name");
        return methodName;
    }

    public String getMethodStatus(String methodObject) {
        String methodStatus = JsonPath.parse(methodObject).read("$.status");
        return methodStatus;
    }

    public String getMethodStartedAt(String methodObject) {
        String methodStartedAt = JsonPath.parse(methodObject).read("$.started-at");
        return methodStartedAt;
    }

    public String getMethodFinished(String methodObject) {
        String methodFinishedAt = JsonPath.parse(methodObject).read("$.finished-at");
        return methodFinishedAt;
    }

    public int getDurationAt(String methodObject) {
        int methodDurationms = JsonPath.parse(methodObject).read("$.duration-ms");
        return methodDurationms;
    }

}
