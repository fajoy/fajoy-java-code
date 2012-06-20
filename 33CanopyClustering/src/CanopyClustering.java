import java.awt.ItemSelectable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class CanopyClustering {

	public static void main(String[] args) throws IOException {
		URL url = CanopyClustering.class.getResource("moveid.dat");
		//System.out.format("%s", url.getFile());
		CanopyClustering c= new CanopyClustering(url.getFile());
		//c.showData();
		//c.showDistance();
		
		/*
		UserModelMean mean=new UserModelMean("1", c.moveData.values());
		//mean.showData();
		for (UserModel data1 : c.moveData.values()) {
			double d=mean.getCosineDistance(data1);
			//System.out.format("UserId=%s MoveIdSize=%d mean distance=%f\n",data1.UserId,data1.MoveIDs.size(),d);
		}
		*/
		//c.getCanopySet();
		//c.showCanopy();
		
		//find t1,t2
		c.batchTest();
	}
	
	private void batchTest(){
		for(double t=0.5;t>=0;t-=0.01){
			this.T2=t;
			this.T1=t;
			this.canopys.clear();
			long st=System.currentTimeMillis();
			this.getCanopySet();
			long et=System.currentTimeMillis();
			System.out.format("t=%f k=%d time=%s\n",t,this.canopys.size(),et-st);
			if(this.canopys.size()==moveData.size())
				break;
		}
	}
	//0~1 0=diff 1=same
	public double T1=0.5;
	public double T2=0.5;
	public Map<String, UserModel> moveData=new LinkedHashMap<String, UserModel>();
	public List<Canopy> canopys=new ArrayList<CanopyClustering.Canopy>();
	public CanopyClustering(String fileName) throws IOException{
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
	public void showData(){
		for (UserModel data : moveData.values()) {
			System.out.format("uid=%s moveid_count=%d\n",data.UserId,data.MoveIDs.size());
		}
	}
	public void showCanopy(){
		int i=1;
		for (Canopy canopy : canopys) {
			System.out.format("index=%d moveid:%s items=%d\n",i++,canopy.center.UserId,canopy.items.size());
		}
	}
	public void showDistance(){
		for (UserModel data1 : moveData.values()) {
			for (UserModel data2 : moveData.values()) {
				double jd= data1.getJaccardDistance(data2);
				double cd=data1.getCosineDistance(data2);
				System.out.format("uid1=%s uid2=%s jd=%f cd=%f\n",data1.UserId,data2.UserId,jd,cd);
			}
		}
	}
	public void getCanopySet(){
		List<UserModel> list=new ArrayList<UserModel>(moveData.values());
		int index=-1;
		while (list.size()>0) {
			index=(index+1)%list.size();
			UserModel take=list.get(index);
			boolean inT2=false;
			for (Canopy canopy:canopys) {
				if(canopy.inT2(take)){
					canopy.items.put(take, take);
					inT2=true;
				}
			}
			list.remove(take);
			if(inT2)
				continue;
			Canopy newCanopy=new Canopy(take);
			canopys.add(newCanopy);
			
			//System.out.format("canopys size %d\n",canopys.size());
			List<UserModel> dup=new ArrayList<UserModel>(moveData.values());
			dup.remove(take);
			for(UserModel p:dup){
				if(newCanopy.inT2(p)){
					newCanopy.items.put(p, p);
					list.remove(p);
					continue;
				}
				if(newCanopy.inT1(p))
					newCanopy.items.put(p, p);
			}
		}		
		
	}
	
	public class Canopy{
		public UserModel center;
		public HashMap<UserModel,UserModel> items=new HashMap<UserModel,UserModel>();
		public Canopy(UserModel obj){
			this.center=obj;
			items.put(obj,obj);
		}
		boolean inT1(UserModel obj){
			//System.out.format("%s %s %f %f \n",this.center.UserId,obj.UserId,CanopyClustering.this.T1,center.getJaccardDistance(obj));
			return CanopyClustering.this.T1>=center.getJaccardDistance(obj);
		}
		boolean inT2(UserModel obj){
			return CanopyClustering.this.T2>=center.getJaccardDistance(obj);
		}
	}
}
