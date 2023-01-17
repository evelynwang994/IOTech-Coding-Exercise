import java.io.FileReader;
import java.io.FileWriter;
import java.time.Instant;
import java.util.Base64;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * 
 */

/**
 * @author Evelyn
 *
 */
public class Solution {

	/**
	 * @param args
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		
		try {
			Object obj = parser.parse(new FileReader("exercise-02/data/data.json"));
			JSONObject jsonObject = (JSONObject) obj;
			
			JSONArray ltDevises = (JSONArray) jsonObject.get("Devices");
			
			Iterator<JSONObject> iterator = ltDevises.iterator();
			
			// Get current time.
            long curTime = Instant.now().getEpochSecond();
            
            // Discard the devices where the timestamp value is before the current time.
            while (iterator.hasNext()) {
            	JSONObject device = iterator.next();
            	String sTimestamp = (String)device.get("timestamp");
            	long time = Long.parseLong(sTimestamp); // timestamp
            	
            	if(time < curTime) {
            		iterator.remove();
            	}
            	
            }
            
            // Get the total of all value entries
            int total = 0;
            Iterator<JSONObject> secondIt = ltDevises.iterator();
            while (secondIt.hasNext()) {
            	JSONObject device = secondIt.next();
            	String value = (String)device.get("value"); // value
            	
            	// base64 to integer
            	byte[] decodedBytes = Base64.getDecoder().decode(value);
            	
            	int iValue = 0;
            	for (byte b : decodedBytes) {
            		iValue = (iValue << 8) + (b & 0xFF);
            	}
            	
            	total = total + iValue;
            }
            
            // Parse the uuid from the info field of each entry
            String[] arrUUID = new String[ltDevises.size()];
            int index = 0;
            Iterator<JSONObject> thirdIt = ltDevises.iterator();
            while (thirdIt.hasNext()) {
            	JSONObject device = thirdIt.next();
            	String info = (String)device.get("Info"); // info
            	String[] arrInfo = info.split("\\s+"); // info is split by space
            	
            	// UUID
            	String uuid = arrInfo[3].substring(5, arrInfo[3].length() - 1);
            	arrUUID[index] = uuid;
            	
            	index++;
            }
            
            // Write result to file
            JSONObject rs = new JSONObject();
            rs.put("ValueTotal", total);
            
            JSONArray ltUUID = new JSONArray();
            
            for(int i = 0; i < arrUUID.length; i++) {
            	ltUUID.add(arrUUID[i]);
            }
            rs.put("UUIDS", ltUUID);
            
            FileWriter file = new FileWriter("output.json");
            file.write(rs.toJSONString());
            
            file.flush();
            file.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
