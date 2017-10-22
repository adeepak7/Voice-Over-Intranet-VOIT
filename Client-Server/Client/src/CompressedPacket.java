import java.io.Serializable;

public class CompressedPacket implements Serializable{
    Code [] codes;
    byte [] compressedData;
    long  len;

    public CompressedPacket(Code [] codes,byte [] compressedData , long len)
    {
        this.codes = codes;
        this.compressedData = compressedData;
        this.len = len;
    }
    public String toString ()
    {
        String str="";
        for (int i = 0; i < 10; i++) {
            str+=codes[i]+"-";
        }
        str+="\n"+compressedData.length+"\n";
        for (int i = 0; i < compressedData.length; i++) {
            str+=compressedData[i]+"-";
        }
        str+="\n";
        str+=len+"\n";
        return str;
    }
}
