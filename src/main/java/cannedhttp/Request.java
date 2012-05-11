/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cannedhttp;

import java.util.Map;

/**
 *
 * @author nicok
 */
public final class Request {
    public final Map<String, String> headers;
    public final String content;

    Request(Map<String, String> headers, String content) {
        this.headers = headers;
        this.content = content;
    }
    
}
