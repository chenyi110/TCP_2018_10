package com.ouc.tcp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ouc.tcp.client.TCP_Receiver_ADT;
import com.ouc.tcp.message.*;
import com.ouc.tcp.tool.TCP_TOOL;

public class TCP_Receiver extends TCP_Receiver_ADT {
	
	int sequence=1;//���ڼ�¼��ǰ�����յİ����
	private TCP_PACKET ackPack;	//�ظ���ACK���Ķ�
	
	/*���캯��*/
	public TCP_Receiver() {
		super();	//���ó��๹�캯��
		super.initTCP_Receiver(this);	//��ʼ��TCP���ն�
	}

	@Override
	//���յ����ݱ������У��ͣ����ûظ���ACK���Ķ�
	public void rdt_recv(TCP_PACKET recvPack) {
		//���У���룬����ACK
		if(CheckSum.computeChkSum(recvPack) == recvPack.getTcpH().getTh_sum()) {
			
			

			
			//����ACK���ĶΣ�����ȷ�Ϻţ�
			tcpH.setTh_ack(recvPack.getTcpH().getTh_seq());
			ackPack = new TCP_PACKET(tcpH, tcpS, recvPack.getSourceAddr());
			tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
			
			//�ظ�ACK���Ķ�
			reply(ackPack);	
			
			//���ظ����ݵ��������Ҫ�������˳��ţ�ȷ���Ƿ�������ظ������ݣ�
			//ȥ�������е�˳���
			int seq = recvPack.getTcpH().getTh_seq();

		
			//�ж��Ƿ����ظ����ݣ����ظ����ݣ������ݲ���data����
			int[] data = recvPack.getTcpS().getData();	
			dataQueue.add(data);
			//�����ڴ����յ�˳���
			sequence=sequence+data.length;
		}

		//���ϲ�Ӧ�á���д�ļ�����������
		if(dataQueue.size() >= 20) 
			deliver_data();	
	}

	@Override
	//�������ݣ�������д���ļ���
	public void deliver_data() {
		//���dataQueue��������д���ļ�
		File fw = new File("recvData.txt");
		BufferedWriter writer;
		
		try {
			writer = new BufferedWriter(new FileWriter(fw, true));
			
			//ѭ�����data�������Ƿ����½�������
			while(!dataQueue.isEmpty()) {
				int[] data = dataQueue.poll();
				
				//������д���ļ�
				for(int i = 0; i < data.length; i++) {
					writer.write(data[i] + "\n");
				}
				
				writer.flush();		//����������
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	//�ظ�ACK���Ķ�
	public void reply(TCP_PACKET replyPack) {
		//���ô�����Ʊ�־
		tcpH.setTh_eflag((byte)0);	//eFlag=0���ŵ��޴���
		
		//�������ݱ�
		client.send(replyPack);		
		
	}
	
}
