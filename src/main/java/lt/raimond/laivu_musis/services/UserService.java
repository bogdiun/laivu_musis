package lt.raimond.laivu_musis.services;

import lt.raimond.laivu_musis.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class UserService extends WebService {
    public static final String CREATE_USER_METHOD = "create_user?";

    public User requestCreateUser(User user) throws IOException, ParseException {
        StringBuilder request = new StringBuilder(CREATE_USER_METHOD);
        request.append("name=").append(user.getName()).append("&");
        request.append("email=").append(user.getEmail());

        String response = getHttpResponseAsString(request.toString());
        return convertJsonToUser(response);
    }

    private User convertJsonToUser(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonUser = (JSONObject) parser.parse(response);

        String name = (String) jsonUser.get("name");
        String email = (String) jsonUser.get("email");
        String id = (String) jsonUser.get("id");

        return new User(id, name, email);
    }
}
