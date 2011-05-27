package com.jaygoel.virginminuteschecker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

public class WebsiteScraper
{

    public static String fetchScreen(final String username, final String password)
    {
        String line = "";

        try
        {
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
            {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                    final java.security.cert.X509Certificate[] certs, final String authType)
                {
                }

                @Override
                public void checkServerTrusted(
                    final java.security.cert.X509Certificate[] certs, final String authType)
                {
                }
            } };

            final String url = "https://www1.virginmobileusa.com/login/login.do";
            // String url = "https://www1.virginmobileusa.com/login/login.do";
            // String url =
            // "https://www1.virginmobileusa.com/myaccount/home.do";

            try
            {
                final SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc
                    .getSocketFactory());
            }
            catch (final Exception e)
            {
                e.getMessage();
            }

            // HttpsURLConnection.setFollowRedirects(true);

            final HttpsURLConnection connection = 
            	(HttpsURLConnection) new URL(url).openConnection();
            connection.setHostnameVerifier(new AllowAllHostnameVerifier());

            // connection.setFollowRedirects(true);

            connection.setDoOutput(true);

            // try {
            //Thread.sleep(5000);
            final OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());
            out.write("loginRoutingInfo=&min=" + username + "&vkey=" + password
                + "&submit=submit");
            out.close();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }

            // connection.connect();

            final InputStreamReader in = new InputStreamReader(
                (InputStream) connection.getContent());

            final BufferedReader buff = new BufferedReader(in);

			StringBuilder sb = new StringBuilder();

			while ((line = buff.readLine()) != null) {
				sb.append(line);
			}

			int mainContentIndex = sb.indexOf("id=\"mainContent\"");
			if (mainContentIndex == -1) 
			{
				line = "";
			}
			else 
			{
				line = sb.substring(mainContentIndex);
			}

            connection.disconnect();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            // System.err.println("exception 83");
            // System.err.println(e.getMessage());
            // System.err.println(line);
            return line;
            // rc.put("isValid", "FALSE");
        }
        // line = null;
        if (line == null)
        {
            line = "";
        }
        // System.err.println(line);
        return line;
    }

    public static Map<String, String> parseInfo(final String line)
    {
        final Map<String, String> rc = new HashMap<String, String>();

        if (line == null)
        {
            rc.put("isValid", "FALSE");
            return rc;
        }

        String srch;
        int start;
        int end;

        srch = "<p class=\"tel\">";
        start = line.indexOf(srch);
        end = line.indexOf("</p>", start);

        if (start < 0)
        {
            rc.put("isValid", "FALSE");
            return rc;
        }
        else
        {
            rc.put("isValid", "TRUE");
        }

        // virginInfo.append("Phone Number: ");
        // virginInfo.append(line.substring(start + srch.length(), end));
        // virginInfo.append("\n");

        rc.put("Phone Number", line.substring(start + srch.length(), end));

        srch = "<h3>Monthly Charge</h3><p>";
        start = line.indexOf(srch);
        end = line.indexOf("</p>", start);

        // virginInfo.append("Monthly Charge: ");
        // virginInfo.append(line.substring(start + srch.length(), end));
        // virginInfo.append("\n");

        rc.put("Monthly Charge", line.substring(start + srch.length(), end));

        srch = "<h3>Current Balance</h3><p>";
        start = line.indexOf(srch);
        end = line.indexOf("</p>", start);

        // virginInfo.append("Current Balance: ");
        // virginInfo.append(line.substring(start + srch.length(), end));
        // virginInfo.append("\n");

        rc.put("Current Balance", line.substring(start + srch.length(), end));

        srch = "<h3>Min. Amount Due</h3><p>";
        start = line.indexOf(srch);
        end = line.indexOf("</p>", start);

        // virginInfo.append("Amount Due: ");
        // virginInfo.append(line.substring(start + srch.length(), end));
        // virginInfo.append("\n");

        if ((start > 0) && (end > 0))
        {
            rc.put("Amount Due", line.substring(start + srch.length(), end));
        }
        // srch = "<h3>Date Due</h3><p>";
        // start = line.indexOf(srch);
        // end = line.indexOf("</p>", start);

        // virginInfo.append("Due Date: ");
        // virginInfo.append(line.substring(start + srch.length(), end));
        // virginInfo.append("\n");

        // if ((start > 0) && (end > 0)) {
        // rc.put("Date Due", line.substring(start + srch.length(), end));
        // }

        srch = "<h3>Charge Will be deducted on</h3><p>";
        start = line.indexOf(srch);
        end = line.indexOf("</p>", start);

        // virginInfo.append("Due Date: ");
        // virginInfo.append(line.substring(start + srch.length(), end));
        // virginInfo.append("\n");

        if ((start > 0) && (end > 0))
        {
            rc.put("Charge Deducted",
                line.substring(start + srch.length(), end));
        }

        srch = "<h3>You will be charged on</h3><p>";
        start = line.indexOf(srch);
        end = line.indexOf("</p>", start);

        if ((start > 0) && (end > 0))
        {
            rc.put("Charged on", line.substring(start + srch.length(), end));
        }

        // rc.put("Charged on", "02/05/11");

        srch = "<p id=\"remaining_minutes\"><strong>";
        start = line.indexOf(srch);
        end = line.indexOf("</p>", start);

        // virginInfo.append("Minutes Used: ");
        // virginInfo.append(line.substring(start + srch.length(),
        // end).replaceFirst("</strong>", ""));
        // virginInfo.append("\n");

        rc.put("Minutes Used", line.substring(start + srch.length(), end)
            .replaceFirst("</strong>", ""));

        // rc.put("info", virginInfo.toString());
        return rc;
    }

    public static Map<String, String> getInfo(final String username, final String password)
    {

        final String line = fetchScreen(username, password);
        // Log.d("DEBUG", "Line: "+line);

        return parseInfo(line);

    }

}
