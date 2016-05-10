package com.sap.als.utils;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import com.google.gson.Gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonBuilder {

	static public Gson create() {
		com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();

		gsonBuilder.registerTypeAdapter(Timestamp.class,
				new JsonDeserializer<Timestamp>() {

					@Override
					public Timestamp deserialize(JsonElement json,
							Type typeOfT, JsonDeserializationContext context)
							throws JsonParseException {
						return new Timestamp(json.getAsLong());
					}
				});

		/*
		 * gsonBuilder.registerTypeAdapter( byte.class, new
		 * JsonDeserializer<byte []>() {
		 * 
		 * @Override public byte [] deserialize(JsonElement json, Type typeOfT,
		 * JsonDeserializationContext context) throws JsonParseException {
		 * 
		 * return new Timestamp(json.getAs()); } });
		 */
		return gsonBuilder.create();
	}
}
