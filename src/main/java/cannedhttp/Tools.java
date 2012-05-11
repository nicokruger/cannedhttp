/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cannedhttp;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author nicok
 */
public final class Tools {
    
    public static String toString(InputStream is) {
        try {
            return CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public static String toString(HttpServletRequest req) {
        try {
            return toString(req.getInputStream());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static Map<String,String> getHeaders(HttpURLConnection httpCon) {
        Map<String, String> headers = Maps.newHashMap();

        for (Map.Entry<String, List<String>> h : httpCon.getHeaderFields().entrySet()) {
            headers.put(h.getKey(), Joiner.on("\n").join(h.getValue()));
        }
        
        return headers;
    }    
    
    public static Map<String, String> getHeaders(HttpServletRequest req) {
        Map<String, String> headers = Maps.newHashMap();
        for (String header : Collections.list(req.getHeaderNames())) {
            headers.put(header, req.getHeader(header));
        }
        
        return headers;
    }
    
    Tools() { };
    
    
}
