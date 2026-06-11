package com.ibm.demo.day4.mongodb;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDbDemo {

	public static void main(String[] args) {

		// Connection URL
		String url = "mongodb://localhost:27017";

		// Create client
		MongoClient mongoClient = MongoClients.create(url);

		// Connect database
		MongoDatabase database = mongoClient.getDatabase("ibm-ems");

		// Connect collection
		MongoCollection<Document> collection = database.getCollection("users");

		// Find all documents
		FindIterable<Document> documents = collection.find();

		// Print documents
		for (Document doc : documents) {
			System.out.println(doc.toJson());
		}

		// Close connection
		mongoClient.close();
	}
}