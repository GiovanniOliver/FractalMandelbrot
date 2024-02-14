//Librerias Java Swing, AWT y Utils
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.util.Arrays;

public class MandelbrotSerial {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1056;
    public static final int DETAIL = 1024;

    private static double top = -1.0;
    private static double left = -2.0;
    private static double zoom = 1.0 / 512.0;

    private static BufferedImage image;
    private static JFrame frame;
    private static JFrame frameTime;

    //Main Principal
    public static void main(String[] args) throws InterruptedException {
        //Damos unos segundos de Delay para la demostracion 
        //de generacion de cada imagen
        Thread.sleep(2000);// 2 segundos
        //Timeamos e iniciamos la generacion del Fractal de forma secuencial
        long startimeS = System.currentTimeMillis();//Inicio
        SerialMandel(); //Funcion Mandelbrot en Secuencial
        long endtimeS = System.currentTimeMillis();//Final
        Thread.sleep(2000);// 2 segundos
        //Frame de Comparacion de Tiempos
        TimerFrame(startimeS,endtimeS);
    }

    //Funcion de control de camara Secuencial
    //Click Izquierdo (Zoom In x2) / Click Derecho (Zoom Out x2)
    private static MouseListener mouseListenerS() {
        return new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    // zoom in 2x
                    left = (event.getX() - WIDTH / 8.0) * zoom + left;
                    top = (event.getY() - HEIGHT / 8.0) * zoom + top;
                    zoom = zoom / 2.0;
                } else {
                    // zoom out 2x
                    left = (event.getX() - WIDTH) * zoom + left;
                    top = (event.getY() - HEIGHT) * zoom + top;
                    zoom = zoom * 2.0;
                }
                calculateImageSerial();
            }
        };
    }

    //Funcion Frame de Timer de Comparación
    private static void TimerFrame(long ST,long SE){
        //Fuente de Letra
        Font fuente = new Font("Calibri", 3, 24);
        
        //JTextArea Time
        JTextArea Time = new JTextArea();    
        
        Time.setFont(fuente); //Seteamos la Fuente de Letra
        Time.setForeground(Color.WHITE); // Color de Letra
        Time.setBackground(Color.BLACK); // Color de Fondo Jframe

        //Texto a Imprimir
        Time.setText("Tiempo en Secuencial: "+(SE-ST)+"ms\n");//Tiempo en Secuencial

        //Titulo de JFrame
        frameTime = new JFrame("Comparacion de Tiempos");
        //Tamaño Jframe
        frameTime.setMinimumSize(new Dimension(275, 90));
        //Posición de ventana en el centro de pantalla
        frameTime.setLocationRelativeTo(null);
        //Añadimos el JTextArea Time
        frameTime.add(Time);
        frameTime.pack();
        frameTime.setVisible(true);
        frameTime.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Imprimimos en Consola (Constancia)
        System.out.println("Tiempo Serial: "+(SE-ST)+"ms");
    }
    
    //Funcion Cálculo de Fractal de Mandelbrot en Secuencial
    private static void calculateImageSerial() {
        //Imprimimos la informacion del Hilo
        printThreadInfo();
        //Bucle for
        for(int y=0;y<HEIGHT;y++){
            double ci = y * zoom + top;
            for (int x = 0; x < WIDTH; ++x) {
                double cr = x * zoom + left;
                double zr = 0.0;
                double zi = 0.0;
                int color = 0x000000; // paint The Mandelbrot Set black
                int i;
                for (i = 0; i < DETAIL; ++i) {
                    double zrzr = zr * zr;//Zr^2
                    double zizi = zi * zi; //Zi^2
                    //Condición: Zr^2 + Zi^2 > 4
                    if (zrzr + zizi >= 4) {
                        //Paleta de colores
                        color = PALETTE[i & 15];
                        break;
                    }
                    zi = 2.0 * zr * zi + ci;
                    zr = zrzr - zizi + cr;
                }
                //Establecemos los pixeles RGB con sus valores indicados
                image.setRGB(x, y, color);
            }
            //Repintamos el frame del Fractal
            frame.repaint();
        }
    }

    //Función Imprimir datos del Hilo Actual
    private static void printThreadInfo() {
        //Imprimimos el hilo que se esta ejecutando actualmente
        System.out.println(Thread.currentThread());
        //Imprimimos....
        //Matriz de elementos de seguimiento de pila
        Arrays.stream(Thread.currentThread().getStackTrace())
            .skip(2) //Descartamos los 2 primeros elementos
            .limit(4) //Límite a 4 el tamaño del Stream
            .forEach(System.out::println); //Iteración
        System.out.println();
    }

    //Funcion Frame del Fractal de Mandelbrot de manera Secuencial
    private static void SerialMandel(){
        //Variable imagen a generar
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        //Jlabel donde se posicionara la imagen
        JLabel label = new JLabel(new ImageIcon(image));
        //Funcion control de camara "mouseListener()"
        label.addMouseListener(mouseListenerS());
        //Titulo de Jframe
        frame = new JFrame("Conjunto de Mandelbrot en Secuencial");
        //Añadimos la imagen a través del label
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Función Generación de Fractal de manera Secuencial
        calculateImageSerial();
    }

    private static final int[] PALETTE = {
            0x00421E0F, 0x0019071A, 0x0009012F, 0x00040449,
            0x00000764, 0x000C2C8A, 0x001852B1, 0x00397DD1,
            0x0086B5E5, 0x00D3ECF8, 0x00F1E9BF, 0x00F8C95F,
            0x00FFAA00, 0x00CC8000, 0x00995700, 0x006A3403,
    };
}
