package com.esp.controller;

import com.esp.model.TwitterUser;
import com.github.scribejava.core.services.HMACSha1SignatureService;
import com.google.common.base.Splitter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequestMapping("")
@Controller
public class Oauth1aBindController {

    private static final String CONSUMER_KEY = "STg43tjxFbILF67E0udzoT5qs";
    private static final String CONSUMER_SECRET = "gWcisn84RxHPQ2QfXwdE8LQN7IU3FEfhjIjR4Rp3n7IPvECdge";
    private static final String CALLBACK_URL = "http://localhost:3000/home/connections/twitter-request-token";

    String requestToken;
    String requestTokenSecret;

    @RequestMapping("")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping("/bind")
    public String bind() throws Exception {
        // Get a temporary token from Twitter
        HttpPost post = new HttpPost("https://api.twitter.com/oauth/request_token");

        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("oauth_callback", CALLBACK_URL));
        parameters.add(new BasicNameValuePair("oauth_consumer_key", CONSUMER_KEY));
        parameters.add(new BasicNameValuePair("oauth_signature_method", "HMAC-SHA1"));
        parameters.add(new BasicNameValuePair("oauth_nonce", "1452456779")); //Simulate to randomly generate a confused string
        parameters.add(new BasicNameValuePair("oauth_version", "1.0"));

        String currentTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
        parameters.add(new BasicNameValuePair("oauth_timestamp", currentTimestamp)); //Randomly generate a timestamp

        // After encoding it into three parts, combining it into a line of baseString, and finally signing it with HMAC-SHA1 algorithm
        String verb = "POST";
        String url = URLEncoder.encode("https://api.twitter.com/oauth/request_token", "utf-8");
        String parameter = URLEncoder.encode("oauth_callback=" + URLEncoder.encode(CALLBACK_URL, "utf-8") + "&" + "oauth_consumer_key=" + CONSUMER_KEY + "&oauth_nonce=1452456779&oauth_signature_method=HMAC-SHA1&oauth_timestamp=" + currentTimestamp + "&oauth_version=1.0", "utf-8");
        String baseString = verb + "&" + url + "&" + parameter;

        parameters.add(new BasicNameValuePair("oauth_signature", generateSignature(baseString, "")));
        post.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));

        // Get temporary token in response
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(post);
        HttpEntity httpEntity = response.getEntity();
        String result = EntityUtils.toString(httpEntity, "utf-8");

        Map<String, String> map = Splitter.on("&").withKeyValueSeparator("=").split(result);
        requestToken = map.get("oauth_token");
        System.out.println(requestToken);
        requestTokenSecret = map.get("oauth_token_secret");

        // redirect to Twitter authorization page

        return "redirect:https://api.twitter.com/oauth/authorize?oauth_token=" + requestToken;
    }
    
    
    
    

    @RequestMapping("/home/connections/twitter-request-token")
    public ModelAndView callback(@RequestParam(name = "oauth_token") String oauthToken,
                                 @RequestParam(name = "oauth_verifier") String oauthVerifier) throws Exception {
        // When the user presses the confirmation button on Twitter, Twitter will lead the user back to the callback url, and by the way tell us which oauthToken verifier

        // Use the verifier just confirmed on Twitter and the temporary token previously applied to exchange accessToken
    	System.out.println(oauthVerifier);
    	
    	
    	HttpPost post = new HttpPost("https://api.twitter.com/oauth/access_token");

        List<BasicNameValuePair> parameters = new ArrayList<>();
  
        parameters.add(new BasicNameValuePair("oauth_token", requestToken));
        parameters.add(new BasicNameValuePair("oauth_consumer_key", CONSUMER_KEY));
        parameters.add(new BasicNameValuePair("oauth_signature_method", "HMAC-SHA1"));
        parameters.add(new BasicNameValuePair("oauth_nonce", "1452456779")); //Simulate to randomly generate a confused string
        parameters.add(new BasicNameValuePair("oauth_verifier", oauthVerifier));
        parameters.add(new BasicNameValuePair("oauth_version", "1.0"));

        String currentTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
        parameters.add(new BasicNameValuePair("oauth_timestamp", currentTimestamp)); //Randomly generate a timestamp
        
        
        

        // After encoding it into three parts, combining it into a line of baseString, and finally signing it with HMAC-SHA1 algorithm
        String verb = "POST";
        String url = URLEncoder.encode("https://api.twitter.com/oauth/access_token", "utf-8");
        String parameter = "oauth_token= " + requestToken + "&oauth_verifier=" + oauthVerifier ;
        String baseString = verb + "&" + url + "&" + parameter;

        parameters.add(new BasicNameValuePair("oauth_signature", generateSignature(baseString, requestTokenSecret)));
        post.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));

        // Get accessToken in response
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(post);
        HttpEntity httpEntity = response.getEntity();
        String result = EntityUtils.toString(httpEntity, "utf-8");
        Map<String, String> map = Splitter.on("&").withKeyValueSeparator("=").split(result);
        String accessToken = map.get("oauth_token");
        String accessTokenSecret = map.get("oauth_token_secret");
        String name=map.get("screen_name");
        

        System.out.println(accessToken);
        System.out.println(accessTokenSecret);
        
        
        //Directly display accessToken and accessTokenSecret on the front-end page, too lazy to do...
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("twitterUser", new TwitterUser(accessToken, accessTokenSecret, name));
        
        return mv;
    }
    
    
  
    

    private String generateSignature(String baseString, String tokenSecret) {
        HMACSha1SignatureService signatureService = new HMACSha1SignatureService();
        return signatureService.getSignature(baseString, CONSUMER_SECRET, tokenSecret);
    }
    
    
    
    
}
