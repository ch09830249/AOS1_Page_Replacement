import java.util.Random;

public class OS {

	public static void main(String[] args) {
	
		System.out.println("Random版本");
		for(int g=10;g<=100;g=g+10) {			
			int[] frames = new int [g];  //產生frames
			int[] ref_string_1 = new int[100000];
			
			//產生reference string(隨機版)
			for(int j=0; j<ref_string_1.length;j++) {			
				ref_string_1[j] = (int) (Math.random()*499+1);//每個page number都是random的
			}
			FIFO(ref_string_1,frames);
			OPT(ref_string_1,frames);
			ESC(ref_string_1,frames);
			Myalg(ref_string_1,frames);
			
		}
		
		
		
		
		System.out.println();
		System.out.println();
		System.out.println("locality版本");
		for(int g=10;g<=100;g=g+10) {			
			   int[] frames = new int [g];  //產生frames
			   //產生reference string(locality版)
			   int latest=0;//指到下次更新的位置
			   int length=0;//區段的長度
			   int head =0;//區段中的第一個數字為多少
			   int[]ref_string_2 = new int [100000];
			   while(latest<100000) {
			       length = (int)(Math.random()*25+25);
			       head = (int)(Math.random()*499+1);
			       for(int i=0;i<length;i++) { //assign進陣列
			    	   ref_string_2[latest]=head;
			    	   latest++;
			    	   head++;
			    	   if(latest>99999)//若下次更新位置已經超過範圍不用再更新
						   break;
			       	}
			       }
			   FIFO(ref_string_2,frames);
			   OPT(ref_string_2,frames);
			   ESC(ref_string_2,frames);
			   Myalg(ref_string_2,frames);
		}
		
		
		System.out.println();
		System.out.println();
		System.out.println("自創版本");
		for(int g=10;g<=100;g=g+10) {			
			   int[] frames = new int [g];//產生frames
			   //為了徹底展現spatial locality帶來的效益
			   int latest=0;//指到下次更新的位置
			   int length=0;//區段的長度
			   int head =0;//區段中的第一個數字為多少
			   int[]ref_string_3 = new int [100000];
		   	   while(latest<99999) {
			   length = (int)(Math.random()*25+25);//長度一樣從25~50隨機取出
			   head = (int)(Math.random()*499+1);//隨機出head第一個數字1~500
			   for(int i=0;i<length;i++) { //assign進陣列
				   ref_string_3[latest]=(int)(Math.random()*(length/2)+head);//並隨機出(head)~(head+(length/2))之間的數
				   latest++;												//並將隨機的數放入string中
				   if(latest>99999)
					   break;
			   	}
			   }
		   	   FIFO(ref_string_3,frames);
		   	   OPT(ref_string_3,frames);
		   	   ESC(ref_string_3,frames);
		   	   Myalg(ref_string_3,frames);
			
		}
					
	}	
	
	
		public static void FIFO(int[]ref_string_1,int[]frames) {//輸入reference string和frames
					int page_fault = 0;
					int updated_index = 0; //更新位置		
					for(int j=0;j<100000;j++) {//挑出某個reference 
						boolean in_frame = false;
						for(int k=0;k<frames.length;k++) {      
							if(ref_string_1[j]==frames[k])//掃描frame中有無此page
								in_frame = true;
						}
						if(in_frame==false) {
							frames[updated_index]= ref_string_1[j];
							updated_index=(updated_index+1)%(frames.length);//當最後一個frame的index+1會超出範圍,透過取餘數回到第一個
							page_fault++;
						}
					}
					System.out.println("{FIFO}number of page : "+frames.length);
					System.out.println("{FIFO}number of page fault : "+page_fault);
					System.out.println();
				}
		
		
		public static void OPT(int[]ref_string_1,int[]frames) {
			int page_fault = 0;		
			
			for(int j=0;j<ref_string_1.length;j++) {//挑出某個要reference的page number
				boolean in_frame = false;
				for(int k=0;k<frames.length;k++) {      
					if(ref_string_1[j]==frames[k])//掃描frame中有無此page
						in_frame = true;
				}
				if(in_frame==false) { //若此page不在此frame中
					page_fault++;
					int farthest = j+1;//將離最遠的記錄下來
					int replaced_index=0;//記錄要被取代掉的frame的index
					for(int l=0;l<frames.length;l++) {//挑出frame中元素與ref_string後面page number的比對
							boolean in_string =false;
							for(int p=j+1;p<ref_string_1.length;p++) {
								if(frames[l]==ref_string_1[p]) {//看reference string此page之後會不會被參考
									in_string=true;
									if(p>farthest) {//將離最遠(晚)被參考的page number其在reference string中的index記起來
										farthest = p;//看誰最晚被參考
										replaced_index = l;//記起此page在frame的哪個位置,因為最晚被參考可以先取代掉
										break;
									}break;
								}
							}if(in_string==false) {//若此page之後都不會被參考
									replaced_index = l;//直接取代
									break;
								}
					}
					frames[replaced_index] = ref_string_1[j];
				}
			}   System.out.println("{OPT}number of page : "+frames.length);
				System.out.println("{OPT}number of page fault : "+page_fault);
				System.out.println();
		 }
		
		
		public static void ESC(int[]ref_string_1,int[]frames) {
			
			int page_fault = 0;
			int diskwrite = 0;
			boolean[] dirty = new boolean[frames.length];//每個frame都有dirty和ref_bit
			boolean[] ref_bit = new boolean[frames.length];
			
			for(int j=0;j<ref_string_1.length;j++) {//挑出某個要reference page
				boolean in_frame = false;
				for(int k=0;k<frames.length;k++) {      
					if(ref_string_1[j]==frames[k])//掃描frame中有無此page
						in_frame = true;
						ref_bit[k]=true;
				}
								
				if(in_frame==false) { //若此page不在此frame中
					page_fault++;
					int replaced_index =0;//要被替換的frame的index
					int found = 0;
					for(int l=0;l<frames.length;l++) { //找出victim page 找 0,0
						if(ref_bit[l]==false&&dirty[l]==false) {
							found = 1;//found=1就是找到了
							replaced_index=l;//紀錄要替換frame的index
							break;
							}
					}
					if(found==0) {
						for(int l=0;l<frames.length;l++) { //找出victim page 找 0,1
							if(ref_bit[l]==false&&dirty[l]==true) {
								diskwrite++;//要寫入disk
								found = 1;
								replaced_index=l;
								break;
								}
						}
						if(found==0) {
							for(int l=0;l<frames.length;l++) {
								ref_bit[l]=false;
							}
							for(int l=0;l<frames.length;l++) { //找出victim page 找 0,0
								if(ref_bit[l]==false&&dirty[l]==false) {
									found = 1;
									replaced_index=l;
									break;
									}
							}
							if(found==0) {
								for(int l=0;l<frames.length;l++) { //找出victim page 找 0,1
									if(ref_bit[l]==false&&dirty[l]==true) {
										diskwrite++;
										found = 1;
										replaced_index=l;
										break;
										}
									}
								}
						}
					} frames[replaced_index]= ref_string_1[j];
					  ref_bit[replaced_index]=true;
					  Random rd = new Random(); //創一個隨機物件
					  dirty[replaced_index]=rd.nextBoolean();//dirty bit 為隨機的
				}				
			}		
			
			System.out.println("{ESC}number of page : "+frames.length);
			System.out.println("{ESC}number of page fault : "+page_fault);
			System.out.println("{ESC}number of diskwrite : "+diskwrite);
			System.out.println();
		}
		
		
		
		public static void Myalg(int[]ref_string_1,int[]frames) {
			
			int page_fault = 0;
		
			for(int j=0;j<ref_string_1.length;j++) {//挑出某個要reference page
				boolean in_frame = false;
				for(int k=0;k<frames.length;k++) {      
					if(ref_string_1[j]==frames[k])//掃描frame中有無此page
						in_frame = true;
				}
				if(in_frame==false) { //若此page不在此frame中
					page_fault++;
					
					
					//看reference string未來的100個page number,若不夠100個就全看(看未來資料)
					int farthest_1 = j+1;
					int replaced_index_1=0;//記錄要被取代掉的frame的index
					for(int l=0;l<frames.length;l++) {//挑出frame中元素與ref_string後面的比對
							boolean in_string =false;
							
							int future =0;//看未來幾個page number
							if((ref_string_1.length-j)<100)//若後面page number數量不夠100
								future = ref_string_1.length-j;//之後的數字都要看
							else
								future = 100;//若夠就看100個page number
							
							for(int p=j+1;p<future+j;p++) {//只往未來看100個page number
								if(frames[l]==ref_string_1[p]) {//看是否有出現在reference string
									in_string=true;
									if(p>farthest_1) {
										farthest_1 = p;
										replaced_index_1 = l;
										break;
									}break;
								}
							}if(in_string==false) {
									replaced_index_1 = l;
									break;
								}
					}
					
					
					
					//看reference string過去所有page number(看過去資料) 越久之前用的,可能是不需要的
					int farthest_2 = j-1;
					int replaced_index_2=0;//記錄要被取代掉的frame的index
					for(int l=0;l<frames.length;l++) {//挑出frame中page number與ref_string前面的比對
							boolean in_string =false;
							for(int p=j-1;p>=0;p--) {//看過去所有資料
								if(frames[l]==ref_string_1[p]) {//看是否有出現在reference string
									in_string=true;
									if(p<farthest_2) {
										farthest_2 = p;
										replaced_index_2 = l;
										break;
									}break;
								}
							}if(in_string==false) {
									replaced_index_2 = l;
									break;
								}
					}
					
					if(frames[replaced_index_2]<frames[replaced_index_1])  //透過兩個方得到兩個index,取page number較小的,把它換掉
						replaced_index_1 = replaced_index_2;				
					
					frames[replaced_index_1] = ref_string_1[j];
				}
			}		
			System.out.println("{Myalg}number of page : "+frames.length);
			System.out.println("{Myalg}number of page fault : "+page_fault);
			System.out.println();			
		}
}

