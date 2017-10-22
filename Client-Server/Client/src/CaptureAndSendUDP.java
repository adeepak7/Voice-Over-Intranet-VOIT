import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * Buffer Size : 4900 bytes
 * Frame Size  :    2 bytes
 *
 */


class CaptureAndSendUDP extends Frame implements KeyListener{

    public static InputStream inputStream = System.in;
    public ByteArrayOutputStream byteStream;
    public  ObjectOutputStream objecStream;
    public static InputReader in = new InputReader( inputStream );
    HuffmanEncoding encoder = new HuffmanEncoding();
    static long pkt=0;
    ServerSocket sc;
    Socket connection;
    TargetDataLine line;
    DataOutputStream outputStream;
    DatagramSocket socket;
    byte []data;
    CaptureAndSendUDP(InetAddress inetAddress , int port){
        try {

            //Create audio stream
            this.addKeyListener(this);
            this.setVisible(true);
            this.setSize(400,400);
            System.out.println("Hello" );
            AudioFormat.Encoding encoding= AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int channels = 1;
            int frameSize =8;
            int sampleSize = 16;
            boolean bigEndian = true;
            AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate,true);
            DataLine.Info info= new DataLine.Info( TargetDataLine.class,format );
            line=(TargetDataLine ) AudioSystem.getLine( info );
            line.open( format,line.getBufferSize() );
            //Create socket
            //socket=new DatagramSocket( 10001 );


        }
        catch ( Exception e ){
            e.printStackTrace();
        }
        run(inetAddress , port);
    }

    public void run(InetAddress inetAddress , int port){
        try {

           // inetAddress = InetAddress.getByName("localhost");

            int frameSizeInBytes = line.getFormat().getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 18;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            System.out.println(bufferLengthInBytes);
            byteStream = new ByteArrayOutputStream();
            objecStream = new ObjectOutputStream(byteStream);
            line.start();
            System.out.println(line.getBufferSize()+" "+line.getFormat().getFrameSize());



            while ( true )
            {
                    data = new byte[6000];

                int numBytes=line.read(  data,0,bufferLengthInBytes );
                System.out.println("Before Decoding :");
                for (int i = 0; i < numBytes; i++) {
                    System.out.print(data[i] + " " );
                }
                System.out.println("");
                   CompressedPacket compressedPacket = encoder.encode(data);
                    byteStream = new ByteArrayOutputStream();

                    objecStream = new ObjectOutputStream(byteStream);

                    objecStream.writeObject(compressedPacket);

                    data = byteStream.toByteArray();

                    System.out.println(data.length);

                    try {
                        if(numBytes>0) {

                            DatagramPacket packet=new DatagramPacket( data,data.length,inetAddress,port);

                            socket.send( packet );
                            pkt++;
                        }
                    }
                    catch ( Exception e ){
                        e.printStackTrace();
                    }
                data = new HuffmanDecoding().decode(compressedPacket);

                System.out.println("\n"+data.length);
                byteStream.close();
                objecStream.close();

            }

        }
        catch ( Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }
    /*
    public static void main( String[] atgs ) {

        new CaptureAndSendUDP();
        System.exit( 0 );
    }*/

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

    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader( InputStream stream ) {
            reader = new BufferedReader( new InputStreamReader( stream ), 32768 );
            tokenizer = null;
        }

        public String next() {
            while ( tokenizer == null || !tokenizer.hasMoreTokens( ) ) {
                try {
                    tokenizer = new StringTokenizer( reader.readLine( ) );
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
            return tokenizer.nextToken( );
        }

        public int nextInt() {
            return Integer.parseInt( next( ) );
        }

    }

}
