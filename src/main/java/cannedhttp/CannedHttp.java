/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cannedhttp;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import fj.F;
import fj.Unit;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author nicok
 */
public class CannedHttp {
    
    public static Router cannedServer(int port, String cannedPath) {
        Server s = new Server();
        HttpConfiguration httpConfig = new HttpConfiguration();
        ServerConnector connector = new ServerConnector(s, new HttpConnectionFactory(httpConfig));
        
        connector.setHost("0.0.0.0");
        connector.setPort(port);
        s.setConnectors(new Connector[] {connector});
        
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.NO_SECURITY);
        servletContext.setContextPath("/");
        Path path = new Path(cannedPath);
        Router router = new Router(path);
        servletContext.addServlet(new ServletHolder(new CannedServlet(path, router)), "/*");
        s.setHandler(servletContext);
        
        try {
            s.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return router;
    }
    
    static final class Path {
        private final String cannedPath;
        public Path(String cannedPath) {
            this.cannedPath = cannedPath;
        }
        public String make(String contextPath, String type) {
            String file;
            if (contextPath.isEmpty() || contextPath.equals("/")) {
                String name = new File(cannedPath).getName();
                file = name + "." + type;
            } else {
                file = cannedPath + "/" + contextPath + "." + type;
            }
            return file;            
        }
    }
}
