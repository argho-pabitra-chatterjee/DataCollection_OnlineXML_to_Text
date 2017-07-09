package project.data.medline.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadXMLFile {

	public static void main(String argv[]) {

		getArticles(1100);
	}

	private static List<String> getArticleIds(int noOfArticles) {
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			URL url = new URL(
					"https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?retmode=xml&db=pubmed&term=mycobacterium&retstart=1&retmax="
							+ noOfArticles);
			URLConnection conn = url.openConnection();
			Document doc1 = dBuilder.parse(conn.getInputStream());

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc1.getDocumentElement().normalize();

			System.out.println("Root element :"
					+ doc1.getDocumentElement().getNodeName());

			System.out.println("Root element :"
					+ doc1.getDocumentElement().getNodeName());

			NodeList nList = doc1.getElementsByTagName("Id");

			System.out.println("----------------------------"
					+ nList.getLength());

			List<String> ids = new ArrayList<String>();

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				// System.out.println("\nCurrent Element :" +
				// nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("Ids : " + nNode.getTextContent());

					ids.add(nNode.getTextContent());

				}
			}
			return ids;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void getArticles(int noOfArticles) {
		try {

			int maxArticleIds = 200;

			List<String> articleIdsLst = getArticleIds(11000);

			int from = 0, to = 0;

			try (PrintWriter pw = new PrintWriter(
					new BufferedWriter(
							new FileWriter(
									new File(
											"D:\\Users\\argho\\project\\SOM\\datacollection\\output\\Articles_"
													+ System.currentTimeMillis()))));) {
				for (; to < articleIdsLst.size()-1; ) {
					from = to;
					to = (from + maxArticleIds) > (articleIdsLst.size() - 1) ? (articleIdsLst
							.size() - 1) : (from + maxArticleIds);

					System.out.println("from:"+from+" ; to:"+to);
					
					StringBuilder articleIdsAppend = new StringBuilder();
					List<String> articleIdsSubLst = articleIdsLst.subList(from, to);
					for (String articelId : articleIdsSubLst) {
						if (articleIdsAppend.length() != 0) {
							articleIdsAppend.append(',');
						}
						articleIdsAppend.append(articelId);
					}

					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

					URL url = new URL(
							"https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?retmode=xml&db=pubmed&id="
									+ articleIdsAppend.toString());
					URLConnection conn = url.openConnection();
					Document doc1 = dBuilder.parse(conn.getInputStream());

					// optional, but recommended
					// read this -
					// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
					doc1.getDocumentElement().normalize();
					

					NodeList nList = doc1
							.getElementsByTagName("MedlineCitation");
					
/*
					System.out.println("Root element :"
							+ doc1.getDocumentElement().getNodeName());

					System.out.println("Root element :"
							+ doc1.getDocumentElement().getNodeName());


					System.out.println("----------------------------"
							+ nList.getLength());*/

					StringBuilder sb = new StringBuilder();

					for (int temp = 0; temp < nList.getLength(); temp++) {

						System.out.println(temp);
						Node nNode = nList.item(temp);

						// System.out.println("\nCurrent Element :" +
						// nNode.getNodeName());

						if (nNode.getNodeType() == Node.ELEMENT_NODE) {

							Element eElement = (Element) nNode;

							if (eElement.getElementsByTagName("AbstractText")
									.item(0) == null) {
								System.out
										.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
								continue;
							}

							/*
							 * System.out.println("Year : " +
							 * eElement.getElementsByTagName("Year").item(0)
							 * .getTextContent());
							 */
							/*
							 * System.out.println("AbstractText : " +
							 * eElement.getElementsByTagName
							 * ("AbstractText").item(0) .getTextContent());
							 */

							sb.append(eElement.getElementsByTagName("Year")
									.item(0).getTextContent());
							sb.append(" || ");
							sb.append(eElement
									.getElementsByTagName("AbstractText")
									.item(0).getTextContent());
							sb.append("\n");

							if (temp % 1000 == 0) {
								pw.append(sb);
								pw.flush();
								sb.setLength(0);
							}

						}
					}

					if (sb.length() > 0) {
						pw.append(sb);
						pw.flush();
						sb = null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
