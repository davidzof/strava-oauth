package org.magneato.strava;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Small webservice to handle callbacks from OAuth2 application. It recovers the
 * authorization code and then signals this to waiting threads.
 * 
 * @author David George
 * @date 25th March 2015
 */
class OAuthHandler implements HttpHandler {
	private String code = null;
	CountDownLatch fLatch = new CountDownLatch(1);

	public void handle(HttpExchange exchange) throws IOException {
		Map<String, Object> params = (Map<String, Object>) exchange
				.getAttribute("parameters");

		code = (String) params.get("code");

		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200, 0);

			OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(("Strava Code is " + code).getBytes());
			responseBody
					.write("\n\nYou can now close your browser".getBytes());
			responseBody.close();
		}
		fLatch.countDown();
	}

	/**
	 * Blocks until response is read by callback thread
	 * 
	 * @return Authorization code
	 */
	public String getCode() {
		try {
			// block until the worker has set the latch to 0:
			fLatch.await();
		} catch (InterruptedException ex) {
			System.out.println(ex);

		}
		return code;
	}
}