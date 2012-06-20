import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KMeanClustering {
	public static void main(String[] args) throws IOException {
		URL url = CanopyClustering.class.getResource("moveid.dat");
		CanopyClustering c= new CanopyClustering(url.getFile());
		c.T1=0.24;
		c.T2=0.24;
		//c.showData();
		//c.showDistance();
		c.getCanopySet();
		List<UserModelMean> ms=c.getMeans();
		for(UserModelMean m:ms){
			m.showData();
		}
		
	}
	
	public class MeanGroup{
		
	}
}
