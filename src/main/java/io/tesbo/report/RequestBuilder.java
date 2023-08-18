package io.tesbo.report;

import okhttp3.*;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RequestBuilder {

     public static String serverURl = "https://reports.tesbo.io/";



    public void getURL(String env)
    {
        if(env.equals("test"))
        {
             serverURl = "http://report-man.appmatictech.com/";
        }else {
            serverURl = "https://reports.tesbo.io/";
        }

    }


    public String createBuild(String key) {
        String buildId = "";
        try {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(serverURl + "api/v1/build/create")
                .method("POST", body)
                .addHeader("x-identity-key", key)
                .build();

            Response response = client.newCall(request).execute();
            JSONObject object = new JSONObject(response.body().string());
            buildId = ((JSONObject) (object.get("data"))).get("build_id").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buildId;
    }

    public Boolean updateResult(String key, String buildId, JSONObject requestBody) {
        Boolean status = null;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);

        client = builder.build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());

        Request request = new Request.Builder()
                .url(serverURl + "api/v1/build/update/" + buildId)
                .method("POST", body)
                .addHeader("x-identity-key", key)
                .addHeader("Content-Type", "application/json")
                .build();
        try {

            Response response = client.newCall(request).execute();
            String a = response.body().string();

            JSONObject resultObject = new JSONObject(a);


            status = (Boolean) resultObject.get("is_error");


        } catch (IOException e) {
e.printStackTrace();
        }

        return status;
    }


    public void closeBuild(String key, String buildId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(serverURl+"api/v1/build/close/"+buildId)
                .method("POST", body)
                .addHeader("x-identity-key", key)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String a = response.body().string();
            System.out.println(a);
            JSONObject resultObject = new JSONObject(a);


            System.out.println((Boolean) resultObject.get("is_error"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




/*

    public void uploadScreenShot(String key, String buildId, String filePath)  {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image","hunger-g6dbe9dfae_1920.jpg",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File("/Users/viralpatel/Downloads/hunger-g6dbe9dfae_1920.jpg")))
                .build();
        Request request = new Request.Builder()
                .url("http://report-man.appmatictech.com/api/v1/upload-image")
                .method("POST", body)
                .addHeader("x-identity-key", "LzZ4EkXbcjUYggnZ81bRxBLXNe4tPzAC")
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
*/


}
