package com.voronin.noapi;

import fi.iki.elonen.NanoHTTPD;

public class MyHTTPD extends NanoHTTPD {

    private static final int PORT = 8080;

    private MainActivity2 mainActivity;

    public MyHTTPD(MainActivity2 activity) {
        super(PORT);
        mainActivity = activity;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

//        mainActivity.showToast("/hello");

        if (uri.equals("/hello")) {
            String response = "<html><body><h1>Hello server</h1></html></body>";
            return newFixedLengthResponse(response);
        }
        return null;
    }
}

//http://192.168.0.0:8080/hello
//http://192.168.200.2:8080/hello
//http://192.168.232.2:8080/hello
//http://10.0.2.15:8080/hello
//http://192.168.1.20:8080/hello

//http://localhost:8080/hello