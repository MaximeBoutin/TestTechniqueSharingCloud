package com.maximeboutin.testtechnique.sharingcloud.utils;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * @brief Classe Permettant de Gérer toutes les demandes à L'API Twitter necessaires à l'application
 */
public class TwitterAPI {

    private static String secretKey = "";
    private static String consumerKey = "";
    private static String bearerToken = "";

    /**
     * @brief Constructeur permettant de set les clés permettant de générer une sessions twitter
     * @param secretKey la clé secrete donnée par l'API twitter
     * @param consumerKey la clé consumer donnée par l'API twitter
     */
    public TwitterAPI(String secretKey, String consumerKey) {
        this.secretKey = secretKey;
        this.consumerKey = consumerKey;
    }

    /**
     * @brief Constructeur par défaut de Twitter API
     */
    public TwitterAPI() {
        // Constructeur vide
    }

    /**
     * @brief Fait une request avec les clés données lors de l'utlisation.
     * @return Set l'attibut bearerToken et le retourne avec la valeur recue lors de la demande à l'API
     */
    public String requestBearerToken() {
        String rep = "";

        if(bearerToken == ""){
            RequestBearerTockenTask task = new RequestBearerTockenTask();
            task.execute(consumerKey, secretKey);
            try {
                rep = task.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.i("TwitterAPI", "ExecutionException Error : " + e.getStackTrace().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.i("TwitterAPI", "InterruptedException Error : " + e.getStackTrace().toString());

            }

            this.bearerToken = rep;
        }else{
            rep = bearerToken;
        }
        return rep;
    }

    /**
     * @brief Demande les trending à l'API twitter.
     * @return Le top 1 TT monde
     */
    public String requestTrending() {
        String rep = "";

        RequestTrendingTask task = new RequestTrendingTask();
        task.execute(consumerKey, secretKey);
        try {
            rep = task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rep;
    }

    public JSONObject searchWithHashtag(String keyword)
    {
        JSONObject rep = null;

        RequestTweetWithHashtag task = new RequestTweetWithHashtag();
        task.execute(keyword);
        try {
            rep = task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rep;
    }

    public List<TwitterMessage> createListOfTwitterMessages(JSONObject twitterResponse) throws JSONException {

        JSONArray listOfMessages = twitterResponse.getJSONArray("statuses");


        List response = new ArrayList();

        for (int i = 0; i < listOfMessages.length(); i++){

            JSONObject message = listOfMessages.getJSONObject(i);

            String text = message.getString("full_text");
            String username = message.getJSONObject("user").getString("name");
            String pictureURL = message.getJSONObject("user").getString("profile_image_url");

            response.add(new TwitterMessage(username, text, pictureURL));

        }
        return response;
    }

    /**
     * @brief Classe privée permettant de faire la requete du bearerToken twitter.
     */
    private class RequestBearerTockenTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... string) {

            // On encode en base64 le bearerToken en accord avec la doc twitter
            String base64encodedBearerToken = Base64.encodeToString((consumerKey + ":" + secretKey).getBytes(), Base64.NO_WRAP);

            // Variables locales de la fonction
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                // On formate la requete HTTP
                url = new URL("https://api.twitter.com/oauth2/token");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Host", "api.twitter.com");
                urlConnection.setRequestProperty("User-Agent", " My Twitter App v1.0.23");
                urlConnection.setRequestProperty("Authorization", "Basic " + base64encodedBearerToken);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                urlConnection.setRequestProperty("Content-Length", "29");

                String requestBody = "grant_type=client_credentials";
                byte[] outputInBytes = requestBody.getBytes("UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write(outputInBytes);
                os.close();

                // On lit l'entete de la reponse
                int responseCode = urlConnection.getResponseCode();
                if(responseCode == 200){

                        BufferedReader rd = new BufferedReader(new InputStreamReader(
                                urlConnection.getInputStream()));
                        String line;
                        String response = "";
                        while ((line = rd.readLine()) != null) {
                            response += line;

                        }

                        JSONObject jsonObject = new JSONObject(response);

                        bearerToken = jsonObject.getString("access_token");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bearerToken;
        }
    }

    /**
     * @brief Classe privée permettant de faire la requete TT Twitter
     */
    private class RequestTrendingTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... string) {
            // Variables locales de la fonction
            URL url = null;
            HttpURLConnection urlConnection = null;
            String response = "";

            try {
                // On formate la requete HTTP
                url = new URL("https://api.twitter.com/1.1/trends/place.json?id=1");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Host", "api.twitter.com");
                urlConnection.setRequestProperty("User-Agent", " My Twitter App v1.0.23");
                urlConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                // On lit l'entete de la reponse
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    // Lit la réponse du serveur
                    while ((line = rd.readLine()) != null) {
                        response += line;

                    }
                    // Get le nom du top 1 TT, le format est [{"trends";[{"name":...}]].
                    response = new JSONArray(response).getJSONObject(0).getJSONArray("trends").getJSONObject(0).getString("name");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    /**
     * @brief Classe privée permettant de faire la recherche via mot clé sur Twitter
     */
    private class RequestTweetWithHashtag extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... string) {
            // Variables locales de la fonction
            URL url = null;
            HttpURLConnection urlConnection = null;
            JSONObject response = null;
            //Enlever le # dans le texte
            String keyword = string[0].substring(1);

            try {
                // On formate la requete HTTP
                url = new URL("https://api.twitter.com/1.1/search/tweets.json?tweet_mode=extended&include_entities=false&q=%23" + keyword);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Host", "api.twitter.com");
                urlConnection.setRequestProperty("User-Agent", " My Twitter App v1.0.23");
                urlConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                // On lit l'entete de la reponse
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    String responseRead = "";

                    // Lit la réponse du serveur
                    while ((line = rd.readLine()) != null) {
                        responseRead += line;

                    }

                    response = new JSONObject(responseRead);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

}

