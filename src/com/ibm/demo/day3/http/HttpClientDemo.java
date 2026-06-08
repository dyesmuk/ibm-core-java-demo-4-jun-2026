package com.ibm.demo.day3.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientDemo {

	public static void main(String[] args) throws IOException, InterruptedException {

		String apiUrl = "https://jsonplaceholder.typicode.com/users/2";

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println(response.statusCode());
		System.out.println(response.body());
		System.out.println(response.headers());
		

	}
}
