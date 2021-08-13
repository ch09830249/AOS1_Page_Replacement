import java.util.Random;

public class OS {

	public static void main(String[] args) {
	
		System.out.println("Random����");
		for(int g=10;g<=100;g=g+10) {			
			int[] frames = new int [g];  //����frames
			int[] ref_string_1 = new int[100000];
			
			//����reference string(�H����)
			for(int j=0; j<ref_string_1.length;j++) {			
				ref_string_1[j] = (int) (Math.random()*499+1);//�C��page number���Orandom��
			}
			FIFO(ref_string_1,frames);
			OPT(ref_string_1,frames);
			ESC(ref_string_1,frames);
			Myalg(ref_string_1,frames);
			
		}
		
		
		
		
		System.out.println();
		System.out.println();
		System.out.println("locality����");
		for(int g=10;g<=100;g=g+10) {			
			   int[] frames = new int [g];  //����frames
			   //����reference string(locality��)
			   int latest=0;//����U����s����m
			   int length=0;//�Ϭq������
			   int head =0;//�Ϭq�����Ĥ@�ӼƦr���h��
			   int[]ref_string_2 = new int [100000];
			   while(latest<100000) {
			       length = (int)(Math.random()*25+25);
			       head = (int)(Math.random()*499+1);
			       for(int i=0;i<length;i++) { //assign�i�}�C
			    	   ref_string_2[latest]=head;
			    	   latest++;
			    	   head++;
			    	   if(latest>99999)//�Y�U����s��m�w�g�W�L�d�򤣥ΦA��s
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
		System.out.println("�۳Ъ���");
		for(int g=10;g<=100;g=g+10) {			
			   int[] frames = new int [g];//����frames
			   //���F�����i�{spatial locality�a�Ӫ��įq
			   int latest=0;//����U����s����m
			   int length=0;//�Ϭq������
			   int head =0;//�Ϭq�����Ĥ@�ӼƦr���h��
			   int[]ref_string_3 = new int [100000];
		   	   while(latest<99999) {
			   length = (int)(Math.random()*25+25);//���פ@�˱q25~50�H�����X
			   head = (int)(Math.random()*499+1);//�H���Xhead�Ĥ@�ӼƦr1~500
			   for(int i=0;i<length;i++) { //assign�i�}�C
				   ref_string_3[latest]=(int)(Math.random()*(length/2)+head);//���H���X(head)~(head+(length/2))��������
				   latest++;												//�ñN�H�����Ʃ�Jstring��
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
	
	
		public static void FIFO(int[]ref_string_1,int[]frames) {//��Jreference string�Mframes
					int page_fault = 0;
					int updated_index = 0; //��s��m		
					for(int j=0;j<100000;j++) {//�D�X�Y��reference 
						boolean in_frame = false;
						for(int k=0;k<frames.length;k++) {      
							if(ref_string_1[j]==frames[k])//���yframe�����L��page
								in_frame = true;
						}
						if(in_frame==false) {
							frames[updated_index]= ref_string_1[j];
							updated_index=(updated_index+1)%(frames.length);//��̫�@��frame��index+1�|�W�X�d��,�z�L���l�Ʀ^��Ĥ@��
							page_fault++;
						}
					}
					System.out.println("{FIFO}number of page : "+frames.length);
					System.out.println("{FIFO}number of page fault : "+page_fault);
					System.out.println();
				}
		
		
		public static void OPT(int[]ref_string_1,int[]frames) {
			int page_fault = 0;		
			
			for(int j=0;j<ref_string_1.length;j++) {//�D�X�Y�ӭnreference��page number
				boolean in_frame = false;
				for(int k=0;k<frames.length;k++) {      
					if(ref_string_1[j]==frames[k])//���yframe�����L��page
						in_frame = true;
				}
				if(in_frame==false) { //�Y��page���b��frame��
					page_fault++;
					int farthest = j+1;//�N���̻����O���U��
					int replaced_index=0;//�O���n�Q���N����frame��index
					for(int l=0;l<frames.length;l++) {//�D�Xframe�������Pref_string�᭱page number�����
							boolean in_string =false;
							for(int p=j+1;p<ref_string_1.length;p++) {
								if(frames[l]==ref_string_1[p]) {//��reference string��page����|���|�Q�Ѧ�
									in_string=true;
									if(p>farthest) {//�N���̻�(��)�Q�ѦҪ�page number��breference string����index�O�_��
										farthest = p;//�ݽֳ̱߳Q�Ѧ�
										replaced_index = l;//�O�_��page�bframe�����Ӧ�m,�]���̱߳Q�Ѧҥi�H�����N��
										break;
									}break;
								}
							}if(in_string==false) {//�Y��page���᳣���|�Q�Ѧ�
									replaced_index = l;//�������N
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
			boolean[] dirty = new boolean[frames.length];//�C��frame����dirty�Mref_bit
			boolean[] ref_bit = new boolean[frames.length];
			
			for(int j=0;j<ref_string_1.length;j++) {//�D�X�Y�ӭnreference page
				boolean in_frame = false;
				for(int k=0;k<frames.length;k++) {      
					if(ref_string_1[j]==frames[k])//���yframe�����L��page
						in_frame = true;
						ref_bit[k]=true;
				}
								
				if(in_frame==false) { //�Y��page���b��frame��
					page_fault++;
					int replaced_index =0;//�n�Q������frame��index
					int found = 0;
					for(int l=0;l<frames.length;l++) { //��Xvictim page �� 0,0
						if(ref_bit[l]==false&&dirty[l]==false) {
							found = 1;//found=1�N�O���F
							replaced_index=l;//�����n����frame��index
							break;
							}
					}
					if(found==0) {
						for(int l=0;l<frames.length;l++) { //��Xvictim page �� 0,1
							if(ref_bit[l]==false&&dirty[l]==true) {
								diskwrite++;//�n�g�Jdisk
								found = 1;
								replaced_index=l;
								break;
								}
						}
						if(found==0) {
							for(int l=0;l<frames.length;l++) {
								ref_bit[l]=false;
							}
							for(int l=0;l<frames.length;l++) { //��Xvictim page �� 0,0
								if(ref_bit[l]==false&&dirty[l]==false) {
									found = 1;
									replaced_index=l;
									break;
									}
							}
							if(found==0) {
								for(int l=0;l<frames.length;l++) { //��Xvictim page �� 0,1
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
					  Random rd = new Random(); //�Ф@���H������
					  dirty[replaced_index]=rd.nextBoolean();//dirty bit ���H����
				}				
			}		
			
			System.out.println("{ESC}number of page : "+frames.length);
			System.out.println("{ESC}number of page fault : "+page_fault);
			System.out.println("{ESC}number of diskwrite : "+diskwrite);
			System.out.println();
		}
		
		
		
		public static void Myalg(int[]ref_string_1,int[]frames) {
			
			int page_fault = 0;
		
			for(int j=0;j<ref_string_1.length;j++) {//�D�X�Y�ӭnreference page
				boolean in_frame = false;
				for(int k=0;k<frames.length;k++) {      
					if(ref_string_1[j]==frames[k])//���yframe�����L��page
						in_frame = true;
				}
				if(in_frame==false) { //�Y��page���b��frame��
					page_fault++;
					
					
					//��reference string���Ӫ�100��page number,�Y����100�ӴN����(�ݥ��Ӹ��)
					int farthest_1 = j+1;
					int replaced_index_1=0;//�O���n�Q���N����frame��index
					for(int l=0;l<frames.length;l++) {//�D�Xframe�������Pref_string�᭱�����
							boolean in_string =false;
							
							int future =0;//�ݥ��ӴX��page number
							if((ref_string_1.length-j)<100)//�Y�᭱page number�ƶq����100
								future = ref_string_1.length-j;//���᪺�Ʀr���n��
							else
								future = 100;//�Y���N��100��page number
							
							for(int p=j+1;p<future+j;p++) {//�u�����Ӭ�100��page number
								if(frames[l]==ref_string_1[p]) {//�ݬO�_���X�{�breference string
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
					
					
					
					//��reference string�L�h�Ҧ�page number(�ݹL�h���) �V�[���e�Ϊ�,�i��O���ݭn��
					int farthest_2 = j-1;
					int replaced_index_2=0;//�O���n�Q���N����frame��index
					for(int l=0;l<frames.length;l++) {//�D�Xframe��page number�Pref_string�e�������
							boolean in_string =false;
							for(int p=j-1;p>=0;p--) {//�ݹL�h�Ҧ����
								if(frames[l]==ref_string_1[p]) {//�ݬO�_���X�{�breference string
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
					
					if(frames[replaced_index_2]<frames[replaced_index_1])  //�z�L��Ӥ�o����index,��page number���p��,�⥦����
						replaced_index_1 = replaced_index_2;				
					
					frames[replaced_index_1] = ref_string_1[j];
				}
			}		
			System.out.println("{Myalg}number of page : "+frames.length);
			System.out.println("{Myalg}number of page fault : "+page_fault);
			System.out.println();			
		}
}

