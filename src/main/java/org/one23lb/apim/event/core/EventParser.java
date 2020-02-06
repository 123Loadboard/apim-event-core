package org.one23lb.apim.event.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Hello world!
 *
 */
public class EventParser
{
    public static JsonObject parse(final String content)
    {
        // As a first implementation, we'll assume that the string is our compact
        // format, i.e. first line is the HTTP metadata and second line is HTTP body.
        final int crlf = content.indexOf("\r\n");

        final String metadata;
        final String body;

        if (crlf < 0)
        {
        	metadata = content;
        	body = null;
        }
        else
        {
        	metadata = content.substring(0, crlf);

        	final String remaining = content.substring(crlf + 2);

        	if (remaining.isEmpty())
        		body = null;
        	else
        		body = remaining;
        }

		final JsonObject root = new JsonObject();

        validate(root, "metadata", metadata);
        validate(root, "body", body);

        // Extract our message-id into a property called "_id" so
        // that if we can do upsert in MongoDB.
        final JsonElement el = root.get("metadata");

        if (el != null && el.isJsonObject())
        {
        	final JsonElement el2 = el.getAsJsonObject().get("message");

            if (el2 != null && el2.isJsonObject())
            {
            	final JsonElement el3 = el2.getAsJsonObject().get("id");

            	if (el3 != null && el3.isJsonPrimitive())
            	{
                	final JsonElement el4 = el.getAsJsonObject().get("http");

                    if (el4 != null && el4.isJsonObject())
                    {
                    	final JsonElement el5 = el4.getAsJsonObject().get("statusCode");
                    	final String prefix = (el5 != null && el5.isJsonPrimitive()) ? "response-" : "request-";

                   		root.addProperty("_id", prefix + el3.getAsString());
                    }
            	}
            }
        }

        return root;
    }

	public static void validate(final JsonObject root, final String propName, final String jsonText)
	{
		if (jsonText == null)
			return;

		try
		{
			final JsonElement el = new JsonParser().parse(jsonText);

			root.add(propName, el);
		}
		catch (final JsonSyntaxException e)
		{
			getInvalidProps(root).addProperty(propName, jsonText);
		}
	}

	protected static JsonObject getInvalidProps(final JsonObject root)
	{
		final String memberName = "invalidJson";
		final JsonElement el = root.get(memberName);

		if (el != null)
			return el.getAsJsonObject();

		final JsonObject jobj = new JsonObject();

		root.add(memberName, jobj);

		return jobj;
	}
}
