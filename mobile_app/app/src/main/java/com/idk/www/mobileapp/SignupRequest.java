package com.idk.www.mobileapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignupRequest extends StringRequest{

    private static final String signupURL = "";
    private Map<String, String> params;

    public SignupRequest(String username, String password, String email, String address, Response.Listener<String> listener){
        super(Method.POST, signupURL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        params.put("address", address);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
