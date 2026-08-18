package com.flower.mitayclient.GUI.screen.PlaceListUtil;


import java.util.LinkedHashMap;
import java.util.Map;

public class PlaceCache {
    private static Map<Integer, PlacesPayload.PlaceInfo> places = new LinkedHashMap<>();

    public static void setPlaces(Map<Integer, PlacesPayload.PlaceInfo> newPlaces) {
        places = newPlaces != null ? newPlaces : new LinkedHashMap<>();
    }

    public static Map<Integer, PlacesPayload.PlaceInfo> getPlaces() {
        return places;
    }

    public static boolean isEmpty() {
        return places.isEmpty();
    }

    public static int size() {
        return places.size();
    }
}