/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cannedhttp;

import fj.Unit;
import fj.data.Either;
import fj.data.Option;
import java.util.Map;

/**
 *
 * @author nicok
 */
public final class Response {
    
    final Option<Either<Unit, Resp>> r;

    Response(Option<Either<Unit, Resp>> r) {
        this.r = r;
    }
    
    public static Response custom(int responseCode, Map<String, String> headers, String response) {
        return new Response(Option.some(Either.<Unit,Resp>right(new Resp(headers, responseCode, response))));
    }
    public static Response next() {
        return new Response(Option.some(Either.<Unit,Resp>left(Unit.unit())));
    }
    public static Response error() {
        return new Response(Option.<Either<Unit, Resp>>none());
    }
    
    public static final class Resp {
        final Map<String, String> headers;
        final int responseCode;
        final String response;

        Resp(Map<String, String> headers, int responseCode, String response) {
            this.headers = headers;
            this.responseCode = responseCode;
            this.response = response;
        }

        
    }
}
