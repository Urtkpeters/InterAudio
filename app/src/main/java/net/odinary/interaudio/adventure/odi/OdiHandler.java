package net.odinary.interaudio.adventure.odi;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public interface OdiHandler
{
    <T> List<T> parse(JSONArray jsonArray) throws JSONException;
}
