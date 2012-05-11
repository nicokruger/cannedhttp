/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cannedhttp;

import cannedhttp.CannedHttp.Path;
import com.google.common.collect.Maps;
import fj.F;
import fj.Unit;
import java.util.Map;

/**
 *
 * @author nicok
 */
public final class Router {
    final Map<String, F<Request, Response>> routes;

    private final Path path;
    public Router(Path path) {
        this.path = path;
        this.routes = Maps.newHashMap();
    }

    public Route on(String url) {
        return new Route(url, this, path);
    }
    
}
