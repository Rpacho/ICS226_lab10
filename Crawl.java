import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Crawl {

	// This is where it will store the link that it will see
	static ArrayList<URI> output = new ArrayList<URI>();
	// this is where it will store the link that already visited
	static Hashtable<String, Boolean> webVisited = new Hashtable<String, Boolean>();

	/*
		This function is where it store the link it found. By using pattern and match, it can Identify the link tag in href element tag.
		If the site is already visited it return or else it store the link in webVisited and output dictionary.

		Return nothing

		Note: Any error that it found will exit the app.
	*/
	public static void storeHtml(String str){
		try{
			synchronized(output){
				Pattern pattern = Pattern.compile("<a href=\"(.*)\"", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(str);
				while (matcher.find()) {
					if(webVisited.get((matcher.group(1))) != null){
						continue;
					}else{
						webVisited.put((matcher.group(1)), true);
						output.add(URI.create(matcher.group(1)));
					}
				}
			}
		}catch (Exception e){
			System.err.println(e);
			System.exit(-1);
		}
	}
		

	public static void main(String[]args) throws InterruptedException, ExecutionException {
			final int MAX_LEVELS = 3; // It crawls until this level
			ArrayList<URI> urlList = new ArrayList<URI>();	// the url list it will visit
			int index = 0;


			while (index < MAX_LEVELS)
			{
				for(String url : args) {
					urlList.add(URI.create(url));
				}
				
				// This is where what server we should contact
				List<HttpRequest> requests = urlList
					.stream()
					.map(url -> HttpRequest.newBuilder(url))
					.map(reqBuilder -> reqBuilder.build())
					.collect(Collectors.toList());
				// use to send request
				HttpClient client = HttpClient.newHttpClient();
				
				// to make a request and execute the storeHtml functionm
				CompletableFuture<?>[] asyncs = requests
					.stream()
					.map(request -> client
						.sendAsync(request, HttpResponse.BodyHandlers.ofString())
						.thenApply(HttpResponse::body)
						.thenAccept(Crawl::storeHtml))
					.toArray(CompletableFuture<?>[]::new);

				CompletableFuture.allOf(asyncs).join();
				
				// Clear the list we visited already
				urlList.clear();

				// Check the output dictionary if the link is already visited then move to the next one. If not
				// visited then store it.
				for(int indexForVisiting = 0; indexForVisiting < output.size(); indexForVisiting++){
					synchronized(output){
						if(webVisited.contains(output.get(indexForVisiting))){
							continue;
						}else{
							webVisited.put(String.valueOf(output.get(indexForVisiting)), true);
							String htmlAdd = args[0] + String.valueOf(output.get(indexForVisiting));
							urlList.add(URI.create(htmlAdd));
						}
					}
				}
				System.out.println(output);
				System.out.println();
				index -= -1;
			}

	}
}