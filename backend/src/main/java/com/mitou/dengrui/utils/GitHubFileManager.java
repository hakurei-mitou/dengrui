package com.mitou.dengrui.utils;

import com.mitou.dengrui.mapper.SyncMapper;
import com.mitou.dengrui.pojo.DTO.GitHubInfo;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitHubFileManager {

//    private String owner = "hakurei-mitou";
//    private String repo = "dengrui-test";
//    private String accessToken = "xxxxxxxxxx";
    private String owner = "";
    private String repo = "";
    private String accessToken = "";

    private static final String downloadDomain = "https://raw.githubusercontent.com";
    private static final String uploadDomain = "https://api.github.com/repos";
    private static final String branchName = "main";

    public static final String cardDataDirName = "cardData-jar-test-1";

    public GitHubFileManager() { }

    @Autowired
    SyncMapper syncMapper;

    @PostConstruct
    public void init() {
        GitHubInfo gitHubInfo = syncMapper.getInfo();
        if (gitHubInfo == null) {
            System.out.println("GitHub information does not exist!");
        } else {
            owner = gitHubInfo.getUsername();
            repo = gitHubInfo.getRepoName();
            accessToken = gitHubInfo.getAccessToken();
        }
    }

    public GitHubFileManager(GitHubInfo gitHubInfo) {
        owner = gitHubInfo.getUsername();
        repo = gitHubInfo.getRepoName();
        accessToken = gitHubInfo.getAccessToken();
    }

    private static List<String> extractNameList(String content) {
        /**
         * content must like : [ {"name":"README.md"...}, {"name":"SQLiteFiles"...}, {"name":...} ... ]
         */
        String pattern = "\"name\":\"([^\"]+)\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);

        List<String> nameList = new ArrayList<String>();
        while (m.find()) {
            String extractedValue = m.group(1);
            nameList.add(extractedValue);
        }
//        System.out.println("nameList = " + nameList.toString());

        return nameList;
    }

    public List<String> getPathNameList(String pathInProject) {

        if (!pathInProject.startsWith("/")) {
            pathInProject = "/" + pathInProject;
        }
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contents%s", owner, repo, pathInProject);

        HttpURLConnection conn = null;
        String responseContent = null;
        try {
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("Authorization", accessToken);

            responseContent = StreamUtils.copyToString(conn.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            // outsider will handler the return value null
            return null;
        } finally {
            conn.disconnect();
        }

        List<String> nameList = extractNameList(responseContent);

        return nameList;
    }

    public void uploadFile(String localPath, String uploadPath) throws IOException {

        if (!uploadPath.startsWith("/")) {
            uploadPath = "/" + uploadPath;
        }

        String apiUrl = uploadDomain + "/" + owner + "/" + repo + "/contents" + uploadPath;

        try {
            // Read file content
            File file = new File(localPath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            String content = Base64.getEncoder().encodeToString(bytes);

            // Prepare HTTP PUT request
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(apiUrl);

            // Request headers
            httpPut.setHeader("Authorization", "token " + accessToken);
            httpPut.setHeader("Content-Type", "application/json");

            // Request body
            String jsonBody = "{\"message\": \"Upload file via API\", " +
                    "\"content\": \"" + content + "\"}";
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                HttpEntity responseEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 201) {
                    System.out.println("File uploaded successfully!");
    //                System.out.println("Response: " + EntityUtils.toString(responseEntity));
                } else {
                    System.err.println("Failed to upload file. Status code: " + response.getStatusLine().getStatusCode());
                    System.err.println("Error response: " + EntityUtils.toString(responseEntity));
                }
            } finally {
                httpClient.close();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public void downloadFile(String pathInProject, String localPath) throws IOException {

        if (!pathInProject.startsWith("/")) {
            pathInProject = "/" + pathInProject;
        }

        String fileURL = downloadDomain + "/" + owner + "/" + repo + "/" + branchName + pathInProject;

        try (InputStream inputStream = new URL(fileURL).openStream();
            OutputStream outputStream = new FileOutputStream(localPath)) {

            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        }

    }


    public Boolean tryConnectGitHub() {
        List<String> pathNameList = getPathNameList("/");
        if (pathNameList == null) {
            System.out.println("Connect GitHub error!");
            return false;
        } else {
            System.out.println("Connect GitHub success!");
            return true;
        }
    }

}
