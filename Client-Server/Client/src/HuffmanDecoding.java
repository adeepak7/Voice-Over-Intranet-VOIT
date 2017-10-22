import java.util.ArrayList;

/**
 * Created by ADMIN on 8/31/2016.
 */

class CodeTree
{


    public  void addCode(Node root,int val,String code)
    {
        if(code.length()==0)
        {
            root.setValue(val);
            return;
        }
        if(code.charAt(0)=='0')
        {
            if(root.l==null)
                root.l = new Node(0,0);
            addCode(root.l,val,code.substring(1,code.length()));
        }
        else if(code.charAt(0)=='1')
        {
            if(root.r==null)
                root.r = new Node(0,0);
            addCode(root.r,val,code.substring(1,code.length()));
        }
    }
}

public class HuffmanDecoding {
    ArrayList<Code> codes = new ArrayList<Code>();
    String code="";
    byte[] encodedData;

    void traversePreorder(Node root,String code)
    {
        if(root!=null&&root.l==null&&root.r==null)
        {
            codes.add(new Code(root.getValue(),code));
        }
        if(root!=null)
        {
            traversePreorder(root.l,code+"0");
            traversePreorder(root.r,code+"1");
        }
    }


    public String reverse(String tmp)
    {
        String s="";
        for(int j=tmp.length()-1;j>=0;--j)
        {
            s+=tmp.charAt(j);
        }
        return s;
    }

    public byte [] decode(CompressedPacket compressedPacket) {
        ArrayList<Code> code_dictonary= new ArrayList<Code>();
        long len =compressedPacket.len;
        System.out.println("Compressed length:"+len);
        for (int i=0;i<10;i++)
        {
            code_dictonary.add(new Code(i,compressedPacket.codes[i].code));
        }
        encodedData = compressedPacket.compressedData;


        String data="";
        for(int i=0;i<encodedData.length;i++)
        {
            String tmp = String.format("%8s",Integer.toBinaryString(encodedData[i]&0xFF)).replace(' ','0');
            data+=reverse(tmp);
        }
        Node root=new Node(0,0);
        Node troot = root;
        int blen=0;
        int k=0;
        int b=0;
        byte [] decoded_data = new byte [(int)len];
        CodeTree codeTree= new CodeTree();
        for(int i=0;i<code_dictonary.size();i++) {
            if(!code_dictonary.get(i).getCode().equals(""))
            codeTree.addCode(root, code_dictonary.get(i).getValue(), code_dictonary.get(i).getCode());
        }

        for(int i=0;i<data.length();)
        {
            while(i<data.length()&&troot.l!=null&&troot.r!=null)
            {
                if(data.charAt(i)=='0')
                    troot=troot.l;
                else if(data.charAt(i)=='1')
                    troot=troot.r;
                i++;
            }
            blen++;
            b = b*10 + troot.getValue();
            if(blen==3)
            {
                decoded_data[k++] = (byte)b;
                blen=0;
                b=0;
                if(k==len)
                    break;
            }
            troot = root;
        }
        return decoded_data;
    }

    public static void main(String[] args) {
        long len = 20;
        Code [] codes = new Code[10];
        for (int i = 0; i < 10; i++) {
            codes[i] = new Code(i,"");
        }
        codes[0] = new Code(0,"110");
        codes[1] = new Code(1,"0");
        codes[2] = new Code(2,"1111");
        codes[3] = new Code(3,"11101");
        codes[4] = new Code(4,"11100");
        codes[8] = new Code(8,"10");
        byte[] data={110,-28,127,-117,-56,-1,22,-111,-1,45,34,-1,91,68,-18,-39,2};
        CompressedPacket compressedPacket = new CompressedPacket(codes , data , len);
        new HuffmanDecoding().decode(compressedPacket);
    }

}
