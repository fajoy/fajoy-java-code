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
		System.out.format("%s", url.getFile());
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
	public Map<String, UserModel> moveData=new LinkedHashMap<String, UserModel>();
	public KMeanClustering(String fileName) throws IOException{
		this.parseData(fileName);
		
	}
	
	public void parseData(String fileName) throws IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader( new FileInputStream(fileName)));
		while(reader.ready()){
			String line=reader.readLine();
			String[] args=line.split("\t", 2);
			if(args==null||args.length<2)
				continue;
			UserModel data=new UserModel();
			data.UserId=args[0];
			String[] moveids=args[1].split(",");
			for(int i=0;i<moveids.length;i++)
			{
				data.MoveIDs.put(moveids[i], 1);
			}
			moveData.put(data.UserId, data);
		}	
	}
}
