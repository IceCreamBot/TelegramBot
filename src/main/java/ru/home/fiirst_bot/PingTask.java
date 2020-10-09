package ru.home.fiirst_bot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
@Setter
public class PingTask {
    private String url = "https://google.com";

@Scheduled(fixedRateString = "1200000")
   public void pingMe(){
        try{
            URL url = new URL(getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            connection.disconnect();
        }
        catch (IOException e){
            e.printStackTrace();
        }
   }
}
