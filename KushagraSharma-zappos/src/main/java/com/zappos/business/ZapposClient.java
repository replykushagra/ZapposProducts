package com.zappos.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.product.bean.ProductBean;

public class ZapposClient {

	private static final String SEARCH_URI = "http://api.zappos.com/Search?key=b05dcd698e5ca2eab4a0cd1eee4117e7db2a10c4";
	private static final String RESULTS = "results";
	private static final String BRAND_NAME = "brandName";
	private static final String PRODUCT_NAME = "productName";
	private static final String PRICE = "price";
	//A boolean variable to check if a list can be made in accordance with user's constraints
	private static boolean isProductListAvailable=false;
	// Actual cost incurred to the user
	static double costToBePaid;
	// Creating a stack for holding the products
	static Stack<ProductBean> listOfProducts = new Stack<ProductBean>();

	public void search(final String term) throws Exception {
		try {
			// Sending a request to the API
			JsonNode jsonReply = sendRequest();
			// Creating a list of ProductList Objects.
			ArrayList<ProductBean> productList = new ArrayList<ProductBean>();
			// For traversing through all the records returned
			int counter = 0;
			// Populating ProductBean
			productList=populateProductBean(jsonReply,productList,counter);
			// Prompting the user to enter number of products and the price
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.println("Enter the number of products");
			int numberOfProducts = Integer.parseInt(br.readLine());
			if(numberOfProducts<0){
				System.out.println("Number of products cannot be negative");
				System.exit(0);
			}
			System.out.println("Enter the cost");
			double cost = Double.parseDouble(br.readLine());
			if(cost<0){
				System.out.println("Cost cannot be negative");
				System.exit(0);
			}
			/* Call to findProductList() For finding the unique combinations of all the products which
			 satisfy user's constraints
			*/
			findProductList(productList, numberOfProducts, cost, 0);
			//If no such list of products are available
			if(isProductListAvailable==false){
				System.out.println("No Product available for the selected criterion");
			}
		}catch (UnknownHostException e) {
			System.out.println("Either Incorrect url or network problem");
		}catch (IOException exception) {
			System.out.println("IO Excpetion");
		} catch (NumberFormatException excpetion) {
			System.out.println("Please enter the numerals");
		} catch (NullPointerException exception) {
			System.out.println("Problem loading data");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JsonNode sendRequest() throws Exception {
		final ClientRequest clientRequest = new ClientRequest(SEARCH_URI);
		// For capturing Response
		final ClientResponse<String> clientResponse = clientRequest
				.get(String.class);
		// Collecting response in JsonNode
		final JsonNode jsonReply = new ObjectMapper().readTree(clientResponse
				.getEntity());
		return jsonReply;
	}

	private ArrayList<ProductBean> populateProductBean(JsonNode jsonReply,ArrayList<ProductBean> productList,int counter){
		while (jsonReply.get(RESULTS).get(counter) != null) {
			ProductBean pBean = new ProductBean();
			// Populating the ProductBean with one record
			pBean.setBrandName(jsonReply.get(RESULTS).get(counter)
					.get(BRAND_NAME).asText());
			double price = Double.parseDouble((jsonReply.get(RESULTS)
					.get(counter).get(PRICE).asText().replace("$", "")));
			pBean.setPrice(price);
			pBean.setProductName(jsonReply.get(RESULTS).get(counter)
					.get(PRODUCT_NAME).asText());
			counter++;
			// Adding all the ProdcutBean objects to productList
			productList.add(pBean);
		}
		return productList;
	}
	private void findProductList(ArrayList<ProductBean> productList,
			int numberOfProducts, double cost, int start) {
		/*
		 * Checking that the cost of all the products in stack is less than the
		 * user entered cost and size of stack is the number of products user
		 * wants to purchase
		 */
		if (costToBePaid <= cost && listOfProducts.size() == numberOfProducts) {
			print(listOfProducts);
			isProductListAvailable=true;
		}
		for (int i = start; i < productList.size(); i++) {
			// Checking if current product can be pushed on stack
			if (costToBePaid + productList.get(i).getPrice() <= cost) {
				listOfProducts.push(productList.get(i));
				costToBePaid += productList.get(i).getPrice();
				/* Making recursive call in such a way so that we perform all
				 the permutations with the ith element.*/
				findProductList(productList, numberOfProducts, cost, i + 1);
				costToBePaid -= (Double) (listOfProducts.pop().getPrice());
			}
		}
		}

	private void print(Stack<ProductBean> listOfProducts) {
		// For calculating the total amount which needs to be paid
		for (ProductBean product : listOfProducts) {
			System.out.println("THE PRODUCT:" + product.getProductName() + " "
					+ ", BRAND:" + product.getBrandName());
		}
		System.out.println("Total cost needed to be paid is:"
				+ costToBePaid+"$");
		System.out.println();
		}
	}