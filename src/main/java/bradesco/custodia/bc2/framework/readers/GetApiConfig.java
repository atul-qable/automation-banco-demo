package bradesco.custodia.bc2.framework.readers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

public class GetApiConfig {

    String requestName;

    public GetApiConfig(String requestName) {
        this.requestName = requestName;
    }

    public JSONObject getApiConfig() {
        String data = "";
        try {
            data = new String(Files.readAllBytes(Paths.get("src/test/java/api/requests/" + requestName + ".json").toAbsolutePath()));
        } catch (Exception baseFolder) {
            try {
                data = new String(Files.readAllBytes(Paths.get(requestName).toAbsolutePath()));
            } catch (Exception tempFolder) {
            }
        }
        JSONObject testConfig = new JSONObject(data);
        return testConfig;
    }

    public String getEndPoint() {
        String finalEndpoint;
        JSONObject object = getApiConfig();
        finalEndpoint = object.getString("endPoint");
        if (object.getString("endPoint").contains("${")) {
            finalEndpoint = getEndPointWithPathParameter(finalEndpoint);
        }
        finalEndpoint = addQueryParameterInURL(finalEndpoint);
        return finalEndpoint;
    }

    public String getEndPointWithPathParameter(String endPoint) {
        String newEndPoint = endPoint;
        JSONObject allPathParameter = getPathParameter();
        Iterator keys = allPathParameter.keys();
        while (keys.hasNext()) {
            String currentDynamicKey = (String) keys.next();
            newEndPoint = newEndPoint.replace("${" + currentDynamicKey + "}", allPathParameter.get(currentDynamicKey).toString());
        }
        return newEndPoint;
    }



    public String getEndPointWithQueryParameter(String endPoint) {
        String newEndPoint = endPoint;
        String[] singleEndpointElement = endPoint.split("\\?");
        String queryParameter = singleEndpointElement[1];
        String[] totalParameterList = queryParameter.split("&");
        for (String singleParameter : totalParameterList) {
            String[] paramList = singleParameter.split("=");
            String paramValue = paramList[1].substring(2, paramList[1].length() - 1);
            newEndPoint = newEndPoint.replace("${" + paramValue + "}", getQueryParameterValue(paramValue));
        }
        return newEndPoint;
    }

    public String addQueryParameterInURL(String endPoint) {
        String newEndPoint = endPoint + "?";
        String finalEndpoint = "";
        try {
            JSONObject object = getAllQueryParameterList();
            Iterator<?> keys = object.keys();
            while (keys.hasNext()) {
                String currentDynamicKey = (String) keys.next();
                String currentDynamicValue = object.get(currentDynamicKey).toString();
                newEndPoint = newEndPoint + currentDynamicKey + "=" + currentDynamicValue + "&";
            }
            finalEndpoint = newEndPoint.substring(0, newEndPoint.length() - 1);
        } catch (Exception e) {
            finalEndpoint = endPoint;
        }
        return finalEndpoint;
    }

    public String getQueryParameterValue(String parameterName) {
        JSONObject object = getApiConfig();
        JSONObject queryParameter = (JSONObject) object.get("queryParameter");
        return queryParameter.getString(parameterName);
    }

    public JSONObject getAllQueryParameterList() {
        JSONObject object = getApiConfig();
        JSONObject queryParameter = (JSONObject) object.get("queryParameter");
        return queryParameter;
    }


    public JSONObject getPathParameter() {

        JSONObject object = getApiConfig();
        JSONObject pathParameter = (JSONObject) object.get("pathParameter");
        return pathParameter;

    }


    public String getMethodType() {
        JSONObject object = getApiConfig();
        return object.getString("methodType");
    }

    public JSONObject getHeader() {
        JSONObject object = getApiConfig();
        return (JSONObject) object.get("header");
    }

    public Map<String, String> getHeaderMap() {
        JSONObject object = getApiConfig();

        JSONObject headerObject = (JSONObject) object.get("header");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = objectMapper.readValue(headerObject.toString(), Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map;
    }

    public JSONObject getBody() {
        JSONObject object = getApiConfig();
        return (JSONObject) object.get("body");
    }

    public JSONObject getSchema() {
        JSONObject object = getApiConfig();
        return (JSONObject) object.get("schema");
    }

    public Map<String, String> getBodyMap() {
        JSONObject object = getApiConfig();

        JSONObject headerObject = (JSONObject) object.get("body");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = objectMapper.readValue(headerObject.toString(), Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map;
    }

}
