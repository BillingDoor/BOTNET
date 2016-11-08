package cs.sii.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;

@Service
public class Conversions {
    /**
     * crea il JSON che rappresenta un generico oggetto
     * @param obj oggetto da codificare in json
     * @return Stringa json che rappresenta l'oggetto in input
     */
	
	
	public Conversions(){
		
	}
	
    public static String toJson(Object obj){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(obj);
    }
    
    /**
     * crea un oggetto a partire da una stringa json
     * @param json la stringa da decodificare
     * @param type tipo dell'oggetto che sarà creato (quello codificato nella stringa in input)
     * @return 
     */
    public static <T> T fromJson(String json, Type t){
   
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, t);    
    }
    

	public static long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}
	
	public static byte[] longToBytes(Long x) {
    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(x);
    return buffer.array();
}

     
}
