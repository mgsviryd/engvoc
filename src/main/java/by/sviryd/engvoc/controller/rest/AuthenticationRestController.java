package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.gson.GsonExcludeStrategies;
import by.sviryd.engvoc.service.UserService;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/json/authentication")
public class AuthenticationRestController {
    @Autowired
    private UserService userService;

    @Autowired
    private GsonExcludeStrategies gsonExcludeStrategies;

    @PostMapping()
    public String getUsers(Principal principal, @AuthenticationPrincipal User user, @RequestBody String json) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray tokensArray = obj.get("tokens").getAsJsonArray();
        Type stringType = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> tokens = gson.fromJson(tokensArray, stringType);
        Map<String, Object> frontendData = new HashMap<>();
        Iterable<User> users;
        if(tokens.isEmpty()){
            users = Collections.emptyList();
        }else{
            users = userService.getUsersByTokens(tokens);
        }
        frontendData.put("users", users);
        if (user != null){
            frontendData.put("user", user);
        }else{
            try{
                OAuth2Authentication auth2 = (OAuth2Authentication) principal;
                String sub = ((HashMap<String, String>) (auth2.getUserAuthentication().getDetails())).get("sub");
                user = userService.findBySub(sub);
                frontendData.put("user", user);
            }catch (Exception e){}
        }
        gson = new GsonBuilder().addSerializationExclusionStrategy(gsonExcludeStrategies.getUserOnlyInfo()).create();
        return gson.toJson(frontendData);
    }
}
