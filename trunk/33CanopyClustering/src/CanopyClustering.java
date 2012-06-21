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
		ItemModelMean mean=new ItemModelMean("1", c.rows.values());
		mean.showData();
		for (RowModel data1 : c.rows.values()) {
			double d=mean.getCosineDistance(data1);
			System.out.format("UserId=%s MoveIdSize=%d mean distance=%f\n",data1.rowId,data1.items.size(),d);
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
			if(this.canopys.size()==rows.size())
				break;
		}
	}
	//0~1 0=diff 1=same
	public double T1=0.5;
	public double T2=0.5;
	public Map<String, RowModel> rows=new LinkedHashMap<String, RowModel>();
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
			RowModel data=new RowModel();
			data.rowId=args[0];
			String[] moveids=args[1].split(",");
			for(int i=0;i<moveids.length;i++)
			{
				if(!moveids[i].isEmpty())
					data.items.put(moveids[i], 1);
			}
			rows.put(data.rowId, data);
		}	
	}
	public void showData(){
		for (RowModel data : rows.values()) {
			System.out.format("uid=%s moveid_count=%d\n",data.rowId,data.items.size());
		}
	}
	public void showCanopy(){
		int i=1;
		for (Canopy canopy : canopys) {
			System.out.format("index=%d moveid:%s items=%d\n",i++,canopy.center.rowId,canopy.items.size());
		}
	}
	public void showDistance(){
		for (RowModel data1 : rows.values()) {
			for (RowModel data2 : rows.values()) {
				double jd= data1.getJaccardDistance(data2);
				double cd=data1.getCosineDistance(data2);
				System.out.format("uid1=%s uid2=%s jd=%f cd=%f\n",data1.rowId,data2.rowId,jd,cd);
			}
		}
	}
	public void getCanopySet(){
		if(canopys.size()>0) return;
		List<RowModel> list=new ArrayList<RowModel>(rows.values());
		int index=-1;
		JaccardDistanceCache cache=new JaccardDistanceCache();
		while (list.size()>0) {
			index=(index+1)%list.size();
			RowModel take=list.get(index);
			boolean inT2=false;
			for (Canopy canopy:canopys) {
				if(inT2(canopy,take,cache)){
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
			List<RowModel> dup=new ArrayList<RowModel>(rows.values());
			dup.remove(take);
			for(RowModel p:dup){
				if(inT2(newCanopy,p,cache)){
					newCanopy.items.put(p, p);
					list.remove(p);
					continue;
				}
				if(inT1(newCanopy,p,cache)){
					newCanopy.items.put(p, p);
				}
			}
		}		
		
	}
	public List<ItemModelMean> getMeans(boolean isPoint){
		List<ItemModelMean> means=new ArrayList<ItemModelMean>();
		int i=0;
		for(Canopy c:canopys){
			ItemModelMean mean=null;
			if(isPoint){
				List<RowModel> centerP=new ArrayList<RowModel>();
				centerP.add(c.center);
				mean=new ItemModelMean(String.valueOf(i),centerP);
			}
			else{
				mean=new ItemModelMean(String.valueOf(i),c.items.values());
			}
			i++;
			means.add(mean);			
		}
		return means;
	}
	public class JaccardDistanceCache {
		public Cache get(RowModel k1,RowModel k2){
			String key=k1.rowId+","+k2.rowId;
			if(cs.containsKey(key))
				return cs.get(key);
			key=k2.rowId+","+k1.rowId;
			if(cs.containsKey(key))
				return cs.get(key);
			Cache c=new Cache(k1, k2);
			cs.put(c.key, c);
			return c;
		}
		private HashMap<String,Cache> cs=new HashMap<String, Cache>();
		public class Cache{
			String key;
			RowModel k1;
			RowModel k2;
			Double d;
			public Cache(RowModel k1,RowModel k2){
				this.k1=k1;
				this.k2=k2;
				key=k1.rowId+","+k2.rowId;
				this.d=k1.getJaccardDistance(k2);
			}
		}
	}
	public boolean inT1(Canopy canopy,RowModel row,JaccardDistanceCache cache){
		JaccardDistanceCache.Cache c= cache.get(canopy.center, row);
		return CanopyClustering.this.T1>c.d;
	}
	public boolean inT2(Canopy canopy,RowModel row,JaccardDistanceCache cache){
		JaccardDistanceCache.Cache c= cache.get(canopy.center, row);
		return CanopyClustering.this.T2>c.d;
	}
	public class Canopy{
		public RowModel center;
		public Map<RowModel,RowModel> items=new LinkedHashMap<RowModel,RowModel>();
		public Canopy(RowModel center){
			this.center=center;
			items.put(center,center);
		}
	}
}
