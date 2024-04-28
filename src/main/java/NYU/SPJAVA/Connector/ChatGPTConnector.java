package NYU.SPJAVA.Connector;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import NYU.SPJAVA.NetworkEntity.ChatGPTResponse;
import NYU.SPJAVA.utils.ImageToBase64;
import NYU.SPJAVA.utils.Property;
import NYU.SPJAVA.utils.Property.CONF;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatGPTConnector {


    //sent an img to ChatGPT, along with the word
    public static ChatGPTResponse sendPostRequest(String imageFilePath,String word) throws IOException {


        String base64Image = ImageToBase64.convertImageToBase64(imageFilePath);
        System.out.println(base64Image);
        String apiKey = Property.get(CONF.GPT_KEY);
        StringBuilder response = null;
        try {
            String jsonPayload = "{"
                    + "\"model\": \"gpt-4-vision-preview\","
                    + "\"messages\": ["
                    + "{"
                    + "\"role\": \"user\","
                    + "\"content\": ["
                    + "{"
                    + "\"type\": \"text\","
                    + "\"text\": \"We are doing a draw and guess game, and would love to use ChatGPT as a backend judge. Basically, we will send you a picture along with a word, and we would expect a score out of 100 and a comment in the format {\\\"score\\\": \\\"xxx\\\", \\\"comment\\\": \\\"xxx\\\"}. The response must be in JSON format and must not include other information. "
                    + "word is "+word+"\""
                    + "},"
                    + "{"
                    + "\"type\": \"image_url\","
                    + "\"image_url\": {"
                    + "\"url\": \"data:image/jpeg;base64," + base64Image + "\""
                    + "}"
                    + "}"
                    + "]"
                    + "}"
                    + "],"
                    + "\"max_tokens\": 300"
                    + "}";

            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey); // Replace YOUR_TOKEN_HERE
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Message : " + connection.getResponseMessage());

            try (
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Body : " + response.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert response != null;
            return parseResponse(response.toString());
        }
    }
    public static ChatGPTResponse parseResponse(String responseBody) {
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray choices = responseJson.getJSONArray("choices");

        if (!choices.isEmpty()) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String contentJsonString = message.getString("content");

            // parse json
            JSONObject contentJson = new JSONObject(contentJsonString);
            String score = contentJson.getString("score");
            String comment = contentJson.getString("comment");

            // put score and comment in a entity class
            ChatGPTResponse contentEntity = new ChatGPTResponse(score, comment);
            System.out.println(contentEntity);
            return contentEntity;
        }else {
            return new ChatGPTResponse("0","Something went wrong, cannot connect to ChatGPT");
        }

    }

    public static void main(String[] args) throws IOException {

        ChatGPTResponse myResponse = sendPostRequest("src/main/resources/Pics/Test.png","smartphone");
        System.out.println(myResponse.getComment());
        System.out.println(myResponse);

    }
}

