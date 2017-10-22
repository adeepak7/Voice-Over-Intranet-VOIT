import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Created by ADMIN on 8/31/2016.
 */

class Node implements Comparable<Node> {
    int value;
    int freq = 0;
    Node l =null;
    Node r = null;
    Node(int value,int freq)
    {
        this.value=value;
        this.freq= freq;
    }
    public void set(Node l,Node r)
    {
        this.l=l;
        this.r=r;
    }
    public int compareTo(@NotNull Node o) {
        if(freq==o.freq)
          return 0;
        else if(freq>o.freq)
            return 1;
        else
            return -1;
    }
    public void inc()
    {
        freq++;
    }
    public int getValue()
    {
        return value;
    }
    public void setValue(int value)
    {
        this.value=value;
    }
    public int getFreq()
    {
        return freq;
    }
    public String toString()
    {
        return value+" - "+freq;
    }
}

class Code implements Serializable
{
    int value;
    String code;
    Code(int val,String code)
    {
        this.value=val;
        this.code=code;
    }
    public String toString()
    {
        return code;
    }
    public String getCode()
    {
        return code;
    }
    public int getValue()
    {
        return value;
    }
}


public class HuffmanEncoding {
    //static ArrayList<Code> codes = new ArrayList<Code>();
     Code codes[] = new Code[10];
    String code="";
    byte [] data;
    long len;

    public static String reverse(String tmp)
    {
        String s="";
        for(int j=tmp.length()-1;j>=0;--j)
        {
            s+=tmp.charAt(j);
        }
        return s;
    }
    void traversePreorder(Node root,String code)
    {
        if(root!=null&&root.l==null&&root.r==null)
        {
            codes[root.getValue()] = new Code(root.getValue(),code);
        }
        else if(root!=null)
        {
            traversePreorder(root.l,code+"0");
            traversePreorder(root.r,code+"1");
        }
    }


 public CompressedPacket encode(byte [] data) {
     for (int i = 0; i < 10; i++) {
         codes[i] = new Code(0,"");
        }
        this.data = data;
        len = data.length;
        String str="";
        for(int i=0;i<data.length;i++)
        {
             if(data[i]<(byte) 10&&data[i]>(byte)0)
                str+="00";
            else if(data[i]<(byte)100&&data[i]>(byte)0)
                str+="0";

            if(data[i]<0)
                str+=((int)data[i]+256);
            else
                str+=data[i];
        }

        ArrayList<Node> nodes = new ArrayList<Node>();
        for(int i=0;i<10;i++)
            nodes.add(new Node(i,0));
        for(int i=0;i<str.length();i++)
        {
            nodes.get(str.charAt(i)-48).inc();
        }
        Collections.sort(nodes);
        Node tmp;
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        for(int i=0;i<10;i++)
        {
            if((tmp = nodes.get(i)).getFreq()!=0)
            pq.add(tmp);
        }

        while(pq.size()>1)
        {
            Node t1,t2;
            t1 = pq.poll();
            t2 = pq.poll();
            Node n =new Node(0,t1.getFreq()+t2.getFreq());
            n.set(t1,t2);
            pq.add(n);
        }
        for(int i=0;i<10;i++)
            codes[i] = new Code(i,"");
        traversePreorder(pq.poll(),"");
     byte [] encodedData = new byte[10000];
     try {
         BitSet bitSet = new BitSet();
         int cnt=0;
         for (int i = 0; i < str.length(); i++) {
             String code = codes[str.charAt(i) - 48].getCode();
             for (int j=0;j<code.length();j++)
             {
                    bitSet.set(cnt, code.charAt(j)-48==1?true:false);
                 cnt++;
             }
         }
       //  System.out.println(bitSet);
         encodedData = bitSet.toByteArray();
    //     System.out.println("L->"+encodedData.length);
         //for(byte b:encodedData)
       //  System.out.print(b+",");
     }
     catch (Exception e){
         System.out.println("abc "+e);
         e.printStackTrace();
     }
     return new CompressedPacket(codes,encodedData,len);
 }

    public static void main(String[] args) {
       // byte [] data={-118 ,118,122,8,118 ,118,122,8,118 ,118,122,8,118 ,118,122,8,118 ,118,-122,8};
       // new HuffmanEncoding();
    }
}
