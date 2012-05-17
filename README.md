cannedhttp
----------

Small, self-contained mock webserver for unit testing webservices over http.
The basic principle is simple - testing webservices requires having an endpoint to connect to, and being able to control what that endpoint returns. It is also beneficial if we could somehow inspect what we push to the endpoint as well.

The most basic usage of cannedhttp is to simply place specially formatted files in the classpath, under a certain package. These file are named for the URL they should represent, and contain data for the HTTP response code, the HTTP headers and the content of the body for the response. They are also parameterised according to the HTTP verb that is used.

So, for example, a file called users.GET inside the directory will be used when serving /users with a GET HTTP verb, and users.POST when POST verb is used. Similairly, placing a file /users/user/eddie.GET will serve as the canned response for the URL /users/user/eddie through the GET verb again. Files/directories are always for the URL they represent.

So, for example, a file structure like follows:

    [f] /canned.GET
    [f] /canned.POST
    [d] /canned
    [f] /canned/users.GET
    [f] /canned/users.POST
    [d] /canned/users
    [f] /canned/users/eddie.GET
    [f] /canned/users/eddie.POST

Will support all the following urls: /, /users, /users/eddie for both GET and POST verbs.

The structure of a file is simple:

    <http response code>
    <headers as key: value>
    <blank line>
    <content>

For example,

    301
    Content-type: text/html
    X-Another-Header: Value

    This is a 301

In addition to this, cannedhttp also supports a router API, allowing you to intercept HTTP requests and inspect the request content, and/or produce a completely custom response. This can be used to ensure that the initial HTTP request is formatted correctly, or the correct headers are set, etc. Here is an example of usage for checking for the presence of headers:



    public void testHeaders() throws Exception {
        
        router.on("/testHeaders").put(new F<Request, Response> () {
            @Override
            public Response f(Request a) {
                System.out.println(a.headers.keySet());
                Assert.assertEquals("application/json; charset=UTF8", a.headers.get("Content-Type"));
                Assert.assertEquals("MyUserAgent", a.headers.get("User-Agent"));
                Assert.assertTrue("Check authentication is http basic auth", a.headers.get("Authorization").contains("Basic "));
                return Response.custom(200, Maps.<String,String>newHashMap(), "<html>Custom response content</html>");
            
        });
    }


Advanced
--------

In more advanced usage scenarios, it could even be used to simulate a part of the webservice you are testing against, by returning different responses depending on request variables and so forth. 

In theory, it could actually be used as a very primitive Java HTTP router, although this use is not recommended and not the intent of the project.
