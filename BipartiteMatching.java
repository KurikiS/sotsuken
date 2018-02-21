package org.apache.giraph.examples;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

import java.io.IOException;

/**
 * Demonstrates the basic Pregel shortest paths implementation.
 */
@Algorithm(
    name = "Shortest paths",
    description = "Finds all shortest paths from a selected vertex"
)
public class BipartiteMatching extends BasicComputation<
    LongWritable, DoubleWritable, FloatWritable, DoubleWritable> {
  /** The shortest paths id */
  public static final LongConfOption SOURCE_ID =
      new LongConfOption("SimpleShortestPathsVertex.sourceId", 1,
          "The shortest paths id");
 

  /**
   * Is this vertex the source id?
   *
   * @param vertex Vertex
   * @return True if the source id
   */
  private boolean isSource(Vertex<LongWritable, ?, ?> vertex) {
    return vertex.getId().get() == SOURCE_ID.get(getConf());
  }

  @Override
  public void compute(
      Vertex<LongWritable, DoubleWritable, FloatWritable> vertex,
      Iterable<DoubleWritable> messages) throws IOException {
    //頂点の値を初期化
	  if(getSuperstep() == 0){
		  vertex.setValue(new DoubleWritable(-1));
	  }
    //4step毎に計算
	  switch((int)getSuperstep() % 4) {	
	  
	  //隣接する頂点に頂点IDを投げる
	  case 0:
		  if(vertex.getId().get() %2 == 0 && vertex.getValue().get() < 0) {
			  double id = vertex.getId().get();
			  sendMessageToAllEdges(vertex,new DoubleWritable(id)); 
			  vertex.voteToHalt();
		  }
		  break;
		  
	  //受け取ったメッセージをランダムに１つ選択
	  //選択したところには頂点IDを送信、選ばれなかったところは-1を送信		
	  case 1:
		  if(vertex.getId().get() %2 == 1 && vertex.getValue().get() < 0) {
			  int mscount = 0,i = 0;
			  double ms = 0;
			  double id = vertex.getId().get();

			  for(DoubleWritable message : messages) {
				  mscount++;
			  }
			  if(mscount != 0){
				  Random rnd = new Random();
				  int rand = rnd.nextInt(mscount);

				  for(DoubleWritable message : messages) {
					  if(i == rand) {
						  ms = message.get();
						  sendMessage(new LongWritable((long)ms),new DoubleWritable(id));
					  }
					  else{
						  ms = message.get();
						  sendMessage(new LongWritable((long)ms),new DoubleWritable(-1));
					  }
				  		i++;
			  		}
			  }
		  }
		  vertex.voteToHalt();
		  break;
		  
		  
	  //メッセージを受信して＋だったらその中から一つ選んで送信
	  //ーだったら停止しないで動かし続ける  
	  case 2:
		  if(vertex.getId().get() %2 == 0 && vertex.getValue().get() < 0) {
			  int mscount = 0,i = 0;
			  long ms = 0;
			  double id = vertex.getId().get();
			  ArrayList<Long> mstrue = new ArrayList<Long>();
			  //double[] mstrue = new double[1000];

			  for(DoubleWritable message : messages) {
				  ms = (long)message.get();
				  if(ms >= 0){
					  mstrue.add(ms);
					  mscount++;
				  }
			  }
			  if(mscount != 0) {
				  Random rnd = new Random();
				  int rand = rnd.nextInt(mscount);
				  sendMessage(new LongWritable(mstrue.get(rand)),new DoubleWritable(id));
				  vertex.setValue(new DoubleWritable((double)mstrue.get(rand)));
				  vertex.voteToHalt();
			  }
		  }
		  break;
		  
	  case 3:
		  if(vertex.getId().get() %2 == 1 && vertex.getValue().get() < 0) {
			  double ms = vertex.getValue().get();
			  for(DoubleWritable message : messages) {
				  ms = message.get();
			  }
			  vertex.setValue(new DoubleWritable(ms));
			  vertex.voteToHalt();
		  }
		  break;
	  }
  }
}
