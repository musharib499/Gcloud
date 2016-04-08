package com.gcloud.gaadi.chat.parser;

import com.gcloud.gaadi.chat.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public interface Parser<T extends Model> {

    public abstract T parse(JSONObject json) throws JSONException;

    // public Collection<IModel> parse(JSONArray array) throws JSONException;
    //
    public ArrayList<Model> parse(String resp) throws JSONException;
}
