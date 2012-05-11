/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cannedhttp;

import cannedhttp.CannedHttp.Path;
import fj.F;
import fj.Unit;

/**
 *
 * @author nicok
 */
public final class Route {
    private final Router router;
    private final String url;
    private final Path path;
    
    public Route(String url, Router router, Path path) {
        this.url = url;
        this.router = router;
        this.path = path;
    }

    public Route post(F<Request, Response> f) {
        this.router.routes.put(path.make(url, "POST"), f);
        return this;
    }

    public Route get(F<Request, Response> f) {
        this.router.routes.put(path.make(url, "GET"), f);
        return this;
    }

    public Route put(F<Request, Response> f) {
        this.router.routes.put(path.make(url, "PUT"), f);
        return this;
    }

    public Route delete(F<Request, Response> f) {
        this.router.routes.put(path.make(url, "DELETE"), f);
        return this;
    }

    public Router build() {
        return router;
    }
    
}
