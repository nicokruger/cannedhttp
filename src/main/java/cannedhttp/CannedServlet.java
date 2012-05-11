/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cannedhttp;

import cannedhttp.CannedHttp.Path;
import cannedhttp.Response.Resp;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import fj.F;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author nicok
 */
public final class CannedServlet extends HttpServlet {
    private final Path path;
    private final Router router;

    CannedServlet(Path path, Router router) {
        this.path = path;
        this.router = router;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp, "GET");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp, "POST");
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp, "DELETE");
    }
    
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp, "PUT");
    }
    
    private void handle(HttpServletRequest req, HttpServletResponse resp, String type) throws IOException {
        String p = path.make(req.getPathInfo(), type);
        if (router.routes.containsKey(p)) {
            handleRoute(p, router.routes.get(p), req, resp);
        } else {
            sendCannedResponse(p, req, resp);
        }
    }

    private void handleRoute(String path, F<Request, Response> f, HttpServletRequest req, HttpServletResponse resp) {
        Response r = f.f(new Request(Tools.getHeaders(req), Tools.toString(req)));
        
        
        if (r.r.isNone()) {
            resp.setStatus(500);
            try {
                PrintWriter sigh = resp.getWriter();
                sigh.append("Error occurred");
                sigh.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            if (r.r.some().isLeft()) {
                sendCannedResponse(path, req, resp);
            } else {
                PrintWriter sigh = null;
                try {
                    Resp response = r.r.some().right().value();
                    resp.setStatus(response.responseCode);
                    for (Map.Entry<String, String> header : response.headers.entrySet()) {
                        resp.setHeader(header.getKey(), header.getValue());
                    }
                    sigh = resp.getWriter();
                    sigh.append(response.response);
                    sigh.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    sigh.close();
                }
            }
        }
    }

    private void sendCannedResponse(String path, HttpServletRequest req, HttpServletResponse resp) {
        try {
            URL filePath = Resources.getResource(path);
            List<String> lines = Resources.readLines(filePath, Charsets.UTF_8);
            int firstBlankLine = Collections.indexOfSubList(lines, Lists.newArrayList(""));
            int responseCode = Integer.valueOf(lines.get(0));
            resp.setStatus(responseCode);
            List<String> headers = lines.subList(1, firstBlankLine);
            for (String headerLine : headers) {
                String[] header = headerLine.split(": ");
                resp.setHeader(header[0], header[1]);
            }
            PrintWriter w = resp.getWriter();
            w.append(Joiner.on("\n").join(lines.subList(firstBlankLine + 1, lines.size())));
            w.close();
        } catch (IOException ioe) {
            resp.setStatus(404);
        }
    }

    
}
