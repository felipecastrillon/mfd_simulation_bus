package main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class DiagramPane extends JFrame{
    JPanel northdpdisplay=new JPanel();
    JPanel southdpcontrol=new JPanel();
    public Diagram mfd = new Diagram("General MFD: Flow-Density(veh/hr-veh/km)",1,1);
    JPanel p = new JPanel();
    JScrollPane scroll = new JScrollPane();
    JButton saveimage=new JButton("Save image");
    JTextField savename=new JTextField(12);
    CardLayout cards=new CardLayout();
    
    FileOutputStream fos;
    PrintStream logf;
    int pointcount=0;
    
    public DiagramPane(int diagtype){
        super();
        setVisible(true);
        setSize(820,780);
        setLayout(new BorderLayout());
        
        if (diagtype == 0){
            add(northdpdisplay, BorderLayout.CENTER);
            northdpdisplay.setLayout(cards);
            northdpdisplay.add("MFD",mfd);
            mfd.inipaint();
        }else{

        }

        
        saveimage.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String imagename=savename.getText();
                File outputfile1=new File(imagename+".png");
                BufferedImage image1=(BufferedImage)(mfd.buffer);

                try{
                    ImageIO.write(image1,"png",outputfile1);
                }
                catch (IOException ioe){}
            }
        });
        
        try{
            fos=new FileOutputStream("temp.dat");
            logf=new PrintStream(fos);
        }catch (Exception e){}
    }
        
    public void printimage(int length, int cyc, double headway){
        String imagename = ("100blockserr"+length+ "length."+cyc+"cycle."+
                            headway+"headway");
        File outputfile1=new File(imagename+".jpg");
        BufferedImage image1=(BufferedImage)(mfd.buffer);

        try{
            ImageIO.write(image1,"jpg",outputfile1);
            //ImageIO.write(image3,"jpg",outputfile3);
        }
        catch (IOException ioe){
        }
    }
        
    public void colorchange(Color c){
        mfd.colorchange(c);
        //varmfd.colorchange(c);
    }
    
    
}
