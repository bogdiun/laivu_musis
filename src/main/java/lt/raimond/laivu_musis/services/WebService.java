package lt.raimond.laivu_musis.services;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public abstract class WebService {

    //        public static final String SERVER_URL = "http://192.168.1.37:8080/";
    public static final String SERVER_URL = "http://miskoverslas.lt/laivu_musis/";

    public static String getHttpResponseAsString(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(SERVER_URL + url);
        HttpResponse response = client.execute(request);

        return convertInputStreamToString(response.getEntity().getContent());
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");

        return writer.toString();
    }
}
