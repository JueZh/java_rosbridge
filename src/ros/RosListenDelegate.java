package ros;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a delegate interface for handling ros topic subscriptions. The {@link #receive(com.fasterxml.jackson.databind.JsonNode, String)}
 * is called every time the topic with which this delegate is associated has new message published.
 * The JSON data, given as a {@link com.fasterxml.jackson.databind.JsonNode}, has four top-level fields:<br/>
 * op: what kind of operation it was; should always be "publish"<br/>
 * topic: to which topic the message was published<br/>
 * type: the ROS message type of the topic<br/>
 * msg: the provided ros message in JSON format. This the primary field you will work with.<br/>
 * In general, you will need to iterate into the msg field of the message and parse out the data you want. For example,
 * for a geometry_msgs/Twist.msg message, you can get out the linear x value as follows:<br/>
 * <code>double x = data.get("msg").get("linear").get("x").asDouble();</code><br/>
 * If a value for a message is an array, the {@link com.fasterxml.jackson.databind.JsonNode} has a method for getting each
 * indexed value: {@link com.fasterxml.jackson.databind.JsonNode#get(int)} instead of by field key, which also returns a
 * {@link com.fasterxml.jackson.databind.JsonNode}. You can get back the size of the array value with the
 * {@link com.fasterxml.jackson.databind.JsonNode#size()} method.
 * @author James MacGlashan.
 */
public interface RosListenDelegate {

	/**
	 * Receives a new published message to a subscribed topic. The JSON data, given as a {@link com.fasterxml.jackson.databind.JsonNode}, has four top-level fields:<br/>
	 * op: what kind of operation it was; should always be "publish"<br/>
	 * topic: to which topic the message was published<br/>
	 * type: the ROS message type of the topic<br/>
	 * msg: the provided ros message in JSON format. This the primary field you will work with.<br/>
	 * This method also receives the full string representation of the received JSON message from ROSBridge.
	 * @param data the {@link com.fasterxml.jackson.databind.JsonNode} containing the JSON data received.
	 * @param stringRep the string representation of the JSON object.
	 */
	void receive(JsonNode data, String stringRep);


	/**
	 * A class for easy conversion to the legacy java_rosbridge {@link #receive(com.fasterxml.jackson.databind.JsonNode, String)}
	 * message format that presented the JSON data
	 * in a {@link java.util.Map} from {@link java.lang.String} to {@link java.lang.Object} instances
	 * in which the values were ether primitives, {@link java.util.Map} objects themselves, or {@link java.util.List}
	 * objects.
	 */
	public static class LegacyFormat{

		/**
		 * A method for easy conversion to the legacy java_rosbridge {@link #receive(com.fasterxml.jackson.databind.JsonNode, String)}
		 * message format that presented the JSON data
		 * in a {@link java.util.Map} from {@link java.lang.String} to {@link java.lang.Object} instances
		 * in which the values were ether primitives, {@link java.util.Map} objects themselves, or {@link java.util.List}
		 * objects.
		 * @param jsonString the source JSON string message that was received
		 * @return a {@link java.util.Map} data structure of the JSON data.
		 */
		public static Map<String, Object> legacyFormat(String jsonString){

			JsonFactory jsonFactory = new JsonFactory();
			Map<String, Object> messageData = new HashMap<String, Object>();
			try {
				ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
				TypeReference<Map<String, Object>> listTypeRef =
						new TypeReference<Map<String, Object>>() {};
				messageData = objectMapper.readValue(jsonString, listTypeRef);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return messageData;
		}
	}



}
