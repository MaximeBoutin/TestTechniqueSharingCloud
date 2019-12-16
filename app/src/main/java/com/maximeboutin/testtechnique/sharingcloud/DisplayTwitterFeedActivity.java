package com.maximeboutin.testtechnique.sharingcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maximeboutin.testtechnique.sharingcloud.utils.TwitterAPI;
import com.maximeboutin.testtechnique.sharingcloud.utils.TwitterMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DisplayTwitterFeedActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_application);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences("SharedPreference_SC", 0);
        String trending = settings.getString("trending", "");
        // Reset the Shared Preference
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("trending", "");
        editor.commit();

        //Si le trendig est déja set, chercher les tweets correspondants, sinon demander le trending actuel.
        TwitterAPI twitterAPI = new TwitterAPI();
        JSONObject tweets = null;
        if(trending != ""){
            tweets = twitterAPI.searchWithHashtag(trending);
        }else{
            twitterAPI.requestBearerToken();
            String hastag = twitterAPI.requestTrending();
            tweets = twitterAPI.searchWithHashtag(hastag);
        }

        if(tweets != null) {
            List<TwitterMessage> messages;
            try{
                messages = twitterAPI.createListOfTwitterMessages(tweets);
                ListView listView = findViewById(R.id.list_view_twitter_feed);

                listView.setAdapter(new TweetAdapter(this, messages));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de la récupération des tweets", Toast.LENGTH_LONG);
            }
        }else {
            Toast.makeText(this, "Erreur lors de la récupération des tweets", Toast.LENGTH_LONG);

        }

    }

    public class TweetAdapter extends ArrayAdapter<TwitterMessage> {

        //tweets est la liste des models à afficher
        public TweetAdapter(Context context, List<TwitterMessage> tweets) {
            super(context, 0, tweets);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_twitter_feed_layout,parent, false);
            }

            TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new TweetViewHolder();
                viewHolder.pseudo = convertView.findViewById(R.id.pseudo);
                viewHolder.text = convertView.findViewById(R.id.text);
                viewHolder.profilePicture = convertView.findViewById(R.id.imageView);

                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            TwitterMessage tweet = getItem(position);

            //il ne reste plus qu'à remplir notre vue
            viewHolder.pseudo.setText(tweet.getPseudo());
            viewHolder.text.setText(tweet.getText());


            URL url = null;
            try {
                GetProfilePictureTask getTask = new GetProfilePictureTask();
                getTask.execute(tweet.getPpUrl());

                Bitmap bmp = getTask.get();
                viewHolder.profilePicture.setImageBitmap(bmp);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        private class TweetViewHolder{
            public TextView pseudo;
            public TextView text;
            public ImageView profilePicture;
        }

        private class GetProfilePictureTask extends AsyncTask<String, Integer, Bitmap>{

            @Override
            protected Bitmap doInBackground(String... strings) {
                URL url = null;
                Bitmap bmp = null;

                try {
                    url = new URL(strings[0]);


                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return bmp;
            }
        }
    }
}
