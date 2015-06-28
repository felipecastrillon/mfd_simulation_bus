package myClasses;
import java.awt.*;
//import java.lang.Number;

/**
 *
 * @author jlaval3
 */
public class XYplot extends javax.swing.JPanel {

    /** Creates new form XYplot */
    public XYplot() {
        initComponents();
    }

    private int xsize, ysize, xs, ys;
    private int tj = 0;
    public double yf,xf;
    public double x0 = 0;
    private Graphics2D bg = null;
    private Image buffer;
    private boolean itsOK = false;

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spnX = new javax.swing.JSpinner();
        spnY = new javax.swing.JSpinner();

        setBackground(new java.awt.Color(255, 255, 255));
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                formAncestorResized(evt);
            }
        });

        spnX.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.8d), null, Double.valueOf(1.0d), Double.valueOf(0.05d)));
        spnX.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnXStateChanged(evt);
            }
        });

        spnY.setModel(new javax.swing.SpinnerNumberModel(0.01d, 0.0d, 2.0d, 0.1d));
        spnY.setRequestFocusEnabled(false);
        spnY.setVerifyInputWhenFocusTarget(false);
        spnY.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnYStateChanged(evt);
            }
        });
        spnY.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                spnYPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(359, Short.MAX_VALUE)
                .addComponent(spnX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(spnY, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(356, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(spnY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 272, Short.MAX_VALUE)
                .addComponent(spnX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void spnXStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnXStateChanged
        setXValMax((Double)spnX.getValue());
        if (itsOK)drawCanvas();
    }//GEN-LAST:event_spnXStateChanged
    private void spnYPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_spnYPropertyChange

    }//GEN-LAST:event_spnYPropertyChange
    private void spnYStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnYStateChanged
        setYValMax((Double)spnY.getValue());
        if (itsOK)drawCanvas();
    }//GEN-LAST:event_spnYStateChanged
    private void formAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_formAncestorResized
        if (itsOK){
            xsize = getSize().width ;
            ysize = getSize().height;
            xf = (xsize-xs)/xValMax;
            yf = (ysize-ys)/yValMax;
            drawCanvas();
        }
    }//GEN-LAST:event_formAncestorResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JSpinner spnX;
    public javax.swing.JSpinner spnY;
    // End of variables declaration//GEN-END:variables

    protected boolean TimeSeries = false;
    public boolean isTimeSeries() {
        return TimeSeries;
    }
    public void setTimeSeries(boolean TimeSeries) {
        this.TimeSeries = TimeSeries;
    }

    protected String title = "MFD";
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
        if (itsOK)drawCanvas();
    }

    protected String xLabel = "veh/km";
    public String getXLabel() {
        return xLabel;
    }
    public void setXLabel(String xLable) {
        this.xLabel = xLable;
    }

    protected String yLabel = "veh/hr";
    public String getYLabel() {
        return yLabel;
    }
    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    protected double xValMax = 1;
    public double getXValMax() {
        return xValMax;
    }
    public void setXValMax(double xValMax) {
        this.xValMax = xValMax;
        xf = (xsize-xs)/xValMax;
    }

    protected double yValMax = 10;
    public double getYValMax() {
        return yValMax;
    }
    public void setYValMax(double yValMax) {
        this.yValMax = yValMax;
        yf = (ysize-ys)/yValMax;
    }

    protected int dotsize = 4;
    public int getDotsize() {
        return dotsize;
    }
    public void setDotsize(int dotsize) {
        this.dotsize = dotsize;
    }

    protected Color dotColor = Color.BLUE;
    public Color getDotColor() {
        return dotColor;
    }
    public void setDotColor(Color dotColor) {
        this.dotColor = dotColor;
    }

    public void ini(){
        xs = 20;
        ys = 20;
        xsize = getSize().width ;
        ysize = getSize().height;
        xf = (xsize-xs)/xValMax;
        yf = (ysize-2*ys)/yValMax;
        buffer = createImage(2*xsize,2*ysize);
        bg = (Graphics2D)buffer.getGraphics();
        drawCanvas();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void drawCanvas(){
        bg.setColor(new Color(250,250,250));
        bg.fillRect(0,0,xsize,ysize);
        bg.setColor(Color.GRAY);
        bg.drawRect(xs-1,-1,xsize,ysize-ys+1);
        //labels
        bg.setColor(Color.BLACK);
        bg.drawString(xLabel, (int)(xsize/2-25),ysize-9);
        bg.drawString(title, (int)(xsize/2-title.length()/2),10);
//        bg.drawString(""+xValMax, xsize-20,ysize-9);
//        bg.drawString(""+yValMax, 0 , 10);
        bg.drawString("0", 9 , ysize-9);
        bg.translate(15,ysize/2+25);
        bg.rotate(-Math.PI/2.0);
        bg.drawString(yLabel,0,0);
        bg.rotate(Math.PI/2.0);
        bg.translate(-15,-ysize/2-25);
        itsOK=true;
    }

    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        if (buffer != null) g2.drawImage (buffer, 0, 0, null);
    }

    public void pinta(double  x, double  y){
        bg.setColor(dotColor);
        if (isTimeSeries()){
            if (tj < xsize-xs-2){
                if (x==1)tj++;
            } else if (x==1){//move to left
                bg.copyArea(xs+1,ys-1,xsize-xs-1,ysize-2*ys+1,-1,0);
            }
            bg.fillRect(xs+tj,ysize-ys-pixY(y),1,dotsize);
        }else{//not a time series
            bg.fillRect(xs+pixX(x),ysize-ys-pixY(y),dotsize,dotsize);
        }
        repaint();
    }
    private int pixX(double x){
        int v = (int)(x*xf)-1;
        return v;
    }
    private int pixY(double x){
        int v = (int)(x*yf)+4;
        return v;
    }
}