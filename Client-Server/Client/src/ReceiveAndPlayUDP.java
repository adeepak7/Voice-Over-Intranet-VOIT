import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ReceiveAndPlayUDP extends Frame implements KeyListener{
   // Socket socket;
    DataInputStream inputStream;
    SourceDataLine line;
    static long pkt=0;
    ByteArrayInputStream byteStream;
    ObjectInputStream objectStream;
    HuffmanDecoding decoder;
    byte data[];
    public ReceiveAndPlayUDP(int port){
        try {

            this.addKeyListener(this);
            this.setVisible(true);
            this.setSize(400,400);
            AudioFormat.Encoding encoding= AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int channels = 1;
            int frameSize = 8;
            int sampleSize = 16;
            boolean bigEndian = true;
            AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
            DataLine.Info info= new DataLine.Info( SourceDataLine.class,format );
            line=(SourceDataLine ) AudioSystem.getLine( info );
            line.open( format,line.getBufferSize() );
            run(port);
        }
        catch ( Exception e ){
            e.printStackTrace();
        }


    }
    public  void run(int port) throws IOException {

        try {

                DatagramSocket socket=new DatagramSocket( port );
                line.start();
                System.out.println(line.getBufferSize()+" "+line.getFormat().getFrameSize());

                decoder = new HuffmanDecoding();
                while ( true ){

           //         data = new byte[6000];

                    byte buf[]=new byte[10000];

                    DatagramPacket packet=new DatagramPacket( buf,buf.length);

                    socket.receive( packet );

                    int len = packet.getLength();
                    data =new byte[packet.getLength()];
                    for (int i = 0; i < len; i++) {
                        data[i] = buf[i];
                    }
                    System.out.println(packet.getLength());

                    byteStream = new ByteArrayInputStream(data);

                    objectStream = new ObjectInputStream(byteStream);

                    CompressedPacket compressedPacket = (CompressedPacket) objectStream.readObject();

                    data = decoder.decode(compressedPacket);
                   pkt++;
                    int numBytes=data.length;
                    int numBytesRemaining = numBytes;

                    while (numBytesRemaining > 0) {
                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    }

                    byteStream.close();
                    objectStream.close();

                }
        }
        catch ( Exception e ){
            e.printStackTrace();
        }

    }
    public static void main( String[] args ) {
        //new ReceiveAndPlayUDP();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println(pkt);
        System.exit(0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(pkt);
        System.exit(0);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println(pkt);
        System.exit(0);
    }
}
