import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.GenericOptionsParser;

public class HadoopWordCount {
	public static class debug{
		public static void log(String msg){
			/*
			   InetAddress serverIp;
		        try {
		        	
		            serverIp = InetAddress.getByName("140.110.141.130");
		            int serverPort=5050;
		            DatagramSocket clientSocket = new DatagramSocket();
		            byte[] sendData = null;
		            sendData = msg.getBytes();
		            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIp, serverPort);
		            clientSocket.send(sendPacket);
		            clientSocket.close();
		            
		        	
		        } catch (IOException e) {}
		        */
		        System.out.println(msg);
		}
	}
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable>
			 {
		
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value,Context output)throws  InterruptedException, IOException  {
			debug.log(String.format("map key=%s", key.toString()));
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				word.set(tokenizer.nextToken());
				output.write(word, one);
			}
			
		}
	}

	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>
			 {
		public void reduce(Text key, Iterator<IntWritable> values,Context output)throws  InterruptedException, IOException {
			debug.log(String.format("reduce key=%s", key.toString()));
			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			output.write(key, new IntWritable(sum));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs =new GenericOptionsParser(conf,args).getRemainingArgs();
		if(otherArgs.length!=2){
			String errMsg=String.format("Usage: %s <in> <out>",HadoopWordCount.class.getSimpleName());
			System.err.println(errMsg);
			System.exit(2);
		}
		Job job = new Job(conf);
		job.setJobName("HadoopWordCount");
		job.setJarByClass(HadoopWordCount.class);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setCombinerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job,new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job,new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true)?0:1);		
	}
}
