import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class fractalxplorer extends JFrame {
    static final int WIDTH=600;
    static final  int HEIGHT=600;
    Canvas canvas;
    BufferedImage fractalImage;
    static final int MAX_ITER=200;
    static final double DEFAULT_ZOOM=100.0;
    static final double DEFAULT_TOP_LEFT_X=-3.0;
    static final double DEFAULT_TOP_LEFT_Y=+3.0;
    double zoomFactor=DEFAULT_ZOOM;
    double topLeftX=DEFAULT_TOP_LEFT_X;
    double topLeftY=DEFAULT_TOP_LEFT_Y;
    public fractalxplorer(){
        setInitialGUIProps();
        addCanvas();
        updateFractral();
        canvas.addKeyStrokeEvents();
    }
    private double getXPos(double x){
        return x/zoomFactor+topLeftX;
    }
    private double getYPos(double y){
        return y/zoomFactor-topLeftY;
    }
    public void updateFractral(){
        for(int x=0;x<WIDTH;x++){
            for(int y=0;y<HEIGHT;y++){
                double c_r=getXPos(x);//the real values are on x axis
                double c_i=getYPos(y);//the imaginary values are on y axis
                int iterCount=computeIteration(c_r,c_i);
                int pixelColor=makeColor(iterCount);
                fractalImage.setRGB(x,y,pixelColor);
            }
        }
        canvas.repaint();
    }
    private int makeColor(int iterCount){
         int color=0b011011100001100101101000;
         int mask= 0b0000000000000010101110111;
         int shiftMag=iterCount/13;

         if(iterCount==MAX_ITER){
             return Color.BLACK.getRGB();

         }
         return color | (mask<<shiftMag);
    }
    private int computeIteration(double c_r,double c_i){
        /*
          let c=c_r+c_i
          let z=z_r+z_i
          z'=z*z+c=(z_r+z_i)*(z_r+z_i)+c_r+c_i
          =i*i=-1
          =z_r*z_r+2*z_r*z_i-z_i*z_i+c_r+c_i
          or,z_r'=z_r*z_r-z_i*z_i+c_r
          or,z_i'=2*z_i*z_r+c_i

         */
        double z_r=0.0;
        double z_i=0.0;
        int iteraton=0;;
        while(z_i*z_i+z_r*z_r<=4.0){
            double z_r_temp=z_r;
            z_r=z_r*z_r-z_i*z_i+c_r;
            z_i=2*z_i*z_r_temp+c_i;
            if(iteraton>=MAX_ITER){
                //point was inside the Mandelbrot Set
                return MAX_ITER;
            }
            iteraton++;

        }
        //complex point was outside the mondelbrot set
        return iteraton;
    }
    private void moveUp(){
        double curHeight=HEIGHT/zoomFactor;
        topLeftY+=curHeight/5;
        updateFractral();
    }
    private void moveDown(){
        double curHeight=HEIGHT/zoomFactor;
        topLeftY-=curHeight/5;
        updateFractral();
    }
    private void moveLeft(){
        double curWidth=WIDTH/zoomFactor;
        topLeftX-=curWidth/5;
        updateFractral();
    }
    private void moveRight(){
        double curWidth=WIDTH/zoomFactor;
        topLeftX+=curWidth/5;
        updateFractral();
    }
    private void addCanvas(){
        canvas=new Canvas();
        fractalImage=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
        canvas.setVisible(true);
        this.add(canvas,BorderLayout.CENTER);
    }
    public void setInitialGUIProps(){
          this.setTitle("Fractal Explorer");
          this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          this.setSize(WIDTH,HEIGHT);
          this.setResizable(false);
          this.setLocationRelativeTo(null);
          this.setVisible(true);
    }
public static void main(String args[]){
    new fractalxplorer();
}
private void adjustZoom(double newX,double newY,double newZoomFactor){
        topLeftX +=newX/zoomFactor;
        topLeftY -=newY/zoomFactor;
        zoomFactor=newZoomFactor;
        topLeftX-=(WIDTH/2)/zoomFactor;
        topLeftY+=(HEIGHT/2)/zoomFactor;
        updateFractral();
}
private class Canvas extends JPanel implements MouseListener{
        public Canvas(){
            addMouseListener(this);
        }
        @Override  public Dimension getPreferredSize(){
            return new Dimension(WIDTH,HEIGHT);
        }
        @Override public void paintComponent(Graphics drawingObject){
            drawingObject.drawImage(fractalImage,0,0,null);
        }
     public void addKeyStrokeEvents(){
            KeyStroke wKey=KeyStroke.getKeyStroke(KeyEvent.VK_W,0);
            KeyStroke aKey=KeyStroke.getKeyStroke(KeyEvent.VK_A,0);
            KeyStroke sKey=KeyStroke.getKeyStroke(KeyEvent.VK_S,0);
            KeyStroke dKey=KeyStroke.getKeyStroke(KeyEvent.VK_D,0);
            Action wPresssed=new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveUp();
                }
            };
            Action aPressed=new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveLeft();
                }
            };
            Action sPressed=new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveDown();
                }
            };
            Action dPressed=new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveRight();
                }
            };
            this.getInputMap().put(wKey,"w_key");
            this.getInputMap().put(aKey,"a_key");
            this.getInputMap().put(sKey,"s_key");
            this.getInputMap().put(dKey,"d_key");
            this.getActionMap().put("w_key",wPresssed);
            this.getActionMap().put("a_key",aPressed);
            this.getActionMap().put("s_key",sPressed);
            this.getActionMap().put("d_key",dPressed);

     }
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        double x = (double) e.getX();
        double y = (double) e.getY();
        switch (e.getButton()) {
            // LEFT -> ZOOM IN
            case MouseEvent.BUTTON1:
                adjustZoom(x, y, zoomFactor * 2);
                break;
            // RIGHT -> ZOOM OUT
            case MouseEvent.BUTTON3:
                adjustZoom(x, y, zoomFactor / 2);
                break;
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
}
