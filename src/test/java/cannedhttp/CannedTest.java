package cannedhttp;

import com.google.common.collect.Maps;
import fj.F;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CannedTest {

    static Router router;
    
    @Test
    public void testRoutesCustomResponse() throws Exception {
        router.on("/route1").get(new F<Request, Response> () {

            @Override
            public Response f(Request a) {
                return Response.custom(200, Maps.<String,String>newHashMap(), "This is custom");
            }
            
        });
        
        R r = httpGet("http://localhost:8089/route1");
        Assert.assertEquals(200, r.responseCode);
        Assert.assertEquals("This is custom", r.response);
    }
    
    @Test
    public void testRoutesFallthrough() throws Exception {
        router.on("/route2").get(new F<Request, Response> () {

            @Override
            public Response f(Request a) {
                return Response.next();
            }
            
        });
        
        R r = httpGet("http://localhost:8089/route2");
        Assert.assertEquals(200, r.responseCode);
        Assert.assertEquals("This is the file", r.response);
    }
    
    @Test(expected=Exception.class)
    public void testRouteError() throws Exception {
        router.on("/route3").get(new F<Request, Response> () {

            @Override
            public Response f(Request a) {
                return Response.error();
            }
            
        });
        
        R r = httpGet("http://localhost:8089/route3");
        Assert.assertEquals(500, r.responseCode);
    }

    @Test
    public void testCanned() throws Exception {
        R r = httpGet("http://localhost:8089/");
        Assert.assertEquals(r.response, "CANNED GET");
        Assert.assertEquals("Test", r.headers.get("X-Custom"));
        r = httpPost("http://localhost:8089/");
        Assert.assertEquals(r.response, "CANNED POST");
        r = httpGet("http://localhost:8089/weirdResponse");
        Assert.assertEquals(301, r.responseCode);
        Assert.assertEquals(r.response, "This is a 301");
    }
    
    @BeforeClass
    public static void before() throws Exception {
        router = CannedHttp.cannedServer(8089, "canned");
    }

    private R httpGet(String surl) throws Exception {
        URL url = new URL(surl);
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        //httpCon.setRequestMethod("GET");
        
        return new R(Tools.toString(httpCon.getInputStream()),
                httpCon.getResponseCode(),
                Tools.getHeaders(httpCon));

    }
    
    private R httpPost(String surl) throws Exception {
        URL url = new URL(surl);
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        
        OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
        out.close();
        
        return new R(Tools.toString(httpCon.getInputStream()),
                httpCon.getResponseCode(), 
                Tools.getHeaders(httpCon));

    }
    
   
    static class R {
        final String response;
        final int responseCode;
        final Map<String, String> headers;
        
        public R(String response, int responseCode, Map<String, String> headers) {
            this.response = response;
            this.responseCode = responseCode;
            this.headers = headers;
        }
    }
}