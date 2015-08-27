package TraClusterAlgorithm;

/**
 * Created by home on 24.08.2015.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.Font;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.*;
import javax.swing.SwingWorker.StateValue;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;

import TraClusterAlgorithm.ClusterGen.LineSegmentCluster;

public class Application extends JFrame {

    /**  */
    private static final long serialVersionUID = -8668818312732181049L;

    // Constants
    public static final int CANVAS_WIDTH  = 640;
    public static final int CANVAS_HEIGHT = 480;

    private Action searchCancelAction;
    private Action browseAction;

    // New Actions
    private Action openAction;
    private Action startAction;
    private Action stopAction;
    private Action drawAction;

    // Components
    //private JPanel drawPanel;
    private DrawArea drawPanel;
    private JTextArea messagesTextArea;
    private JTextField filePathTextField;
    private JButton stopButton;
    private JButton drawButton;

    private ComputationWorker computationWorker;

    // New JComponent to draw lines
    public class DrawArea extends JPanel {
        private static final long serialVersionUID = 1L;

        // parameters to transform coordinates
        private double xL;
        private double yL; // (xL,yL) lower left corner
        private double xR;
        private double yR; // (xR,yR) upper right corner

        //TEMP
        private double x = 0;
        private double y = 0;

        private ArrayList<Trajectory> trajectoryAL;
        //private ArrayList<LineSegmentCluster> clusterRepresentativeTrajectoryAL;
        private ArrayList<Cluster> clusterRepresentativeTrajectoryAL;

        private JTextArea messagesTextArea;

        public DrawArea(JTextArea messagesTextArea) {
            /*
            this.xL = Integer.valueOf(0);
            this.yL = Integer.valueOf(0);
            this.xR = Integer.valueOf(1000);
            this.yR = Integer.valueOf(1000);
            */
            this.xL = 0.0;
            this.yL = 0.0;
            this.xR = 1000.0;
            this.yR = 1000.0;

            this.messagesTextArea = messagesTextArea;
        }

        public DrawArea(double xL, double yL,
                        double xR, double yR,
                        JTextArea messagesTextArea) {
            if (xL != xR) {
                this.xL = xL;
                this.xR = xR;
            } else {
                this.xL = 0.0;
                this.xR = 1000.0;
            }

            if (yL != yR) {
                this.yL = yL;
                this.yR = yR;
            } else {
                this.yL = 0.0;
                this.yR = 1000.0;
            }

            this.messagesTextArea = messagesTextArea;

        }

        public void setTrajectory(ArrayList<Trajectory> trajectory) {
            this.trajectoryAL = trajectory;
        }

        public void setClusterRepresentativeTrajectory(ArrayList<Cluster> clusterRepresentativeTrajectory) {
            this.clusterRepresentativeTrajectoryAL = clusterRepresentativeTrajectory;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);     // paint parent's background
            setBackground(Color.WHITE);  // set background color for this JPanel

            /*
            // Custom painting codes.
            g.setColor(Color.GREEN);    // set the drawing color
            g.drawLine(0, 0, 10, 10);
            g.drawLine(30, 40, 100, 200);
            */

            g.setColor(Color.BLUE);
            //g.drawLine(0, 0, 50, 50);
            //g.drawLine(10, 0, 10, 100);

            //Graphics2D g2d = (Graphics2D) g;
            Graphics2D g2d = (Graphics2D) g.create();

            AffineTransform tform = new AffineTransform();
            //AffineTransform tform = AffineTransform.getTranslateInstance( - xL*getWidth()/(xR - xL), -yR * getHeight()/(yL-yR));
            //tform.scale(getWidth() / (xR - xL), getHeight() / (yL - yR));
            //AffineTransform tform = AffineTransform.getScaleInstance(getWidth() / (xR - xL), getHeight() / (yL - yR));
            //tform.translate( - xL*getWidth()/(xR - xL), -yR * getHeight()/(yL-yR));

            //tform.translate( - xL*getWidth()/(xR - xL), -yR * getHeight()/(yL-yR));
            //tform.scale(getWidth() / (xR - xL), getHeight() / (yL - yR));

            tform.translate( 0, getHeight());
            tform.scale( 1, -1);

            g2d.setTransform(tform);


            double newX = x*getWidth() / (xR - xL) - xL*getWidth()/(xR - xL);
            double newY = y*getHeight() / (yL - yR) - yR * getHeight()/(yL-yR);

            /*
            System.out.println("Researching coordinate transformation");
            System.out.println("getWidth(): " + getWidth());
            System.out.println("getHeight(): " + getHeight());
            System.out.println("new x: " + newX);
            System.out.println("new y: " + newY);

            System.out.println("scale x: " + (getWidth() / (xR - xL)));
            System.out.println("translate x: " + (- xL*getWidth()/(xR - xL)));
            System.out.println("scale y: " + (getHeight() / (yL - yR)));
            System.out.println("translate y: " + (-yR * getHeight()/(yL-yR)));
            */

            x = newX;
            y = newY;

            /*
            //this.messagesTextArea.append("\nInside paintComponent()\n");
            System.out.println("Inside paintComponent()");
            System.out.println("\ngetWidth() / (xR - xL)");
            System.out.println(getWidth());
            System.out.println((xR - xL));
            System.out.println( (double) (getWidth() / (xR - xL)));
            System.out.println("\ngetHeight() / (yL - yR)");
            System.out.println(getHeight());
            System.out.println((yL - yR));
            System.out.println( (double) (getHeight() / (yL - yR)));
            */

            g2d.setColor(Color.GREEN);    // set the drawing color
            //g2d.drawLine(-200, -200, 500, 500);
            //g2d.drawLine(0, 0, 500, -500);
            //g2d.drawLine(-200, 200, 400, -400);

            g2d.dispose();

            //New code
            g.setColor(Color.GREEN);
            //customDrawLine(g, -200, -200, 500, 500);

            //#TEMP
            //System.out.println("Befor entering condition");
            //System.out.println(trajectoryAL);
            //System.out.println(trajectoryAL.isEmpty());
            //System.out.println(clusterRepresentativeTrajectoryAL.isEmpty());


            if (trajectoryAL != null
                    && clusterRepresentativeTrajectoryAL != null
                    && trajectoryAL.isEmpty() != true
                    && clusterRepresentativeTrajectoryAL.isEmpty() != true) {

                //#TEMP
                //System.out.println("Entering condition");

                // Draw trajectory lines
                g.setColor(Color.GREEN);
                for (int i = 0; i < trajectoryAL.size();i++) {
                    for(int m=0; m<trajectoryAL.get(i).getM_pointArray().size()-2 ;m++) {
                        int startX = (int)trajectoryAL.get(i).getM_pointArray().get(m).getM_coordinate(0);
                        int startY = (int)trajectoryAL.get(i).getM_pointArray().get(m).getM_coordinate(1);
                        int endX = (int)trajectoryAL.get(i).getM_pointArray().get(m+1).getM_coordinate(0);
                        int endY = (int)trajectoryAL.get(i).getM_pointArray().get(m+1).getM_coordinate(1);
                        //g2.drawLine(startX, startY, endX, endY);
                        customDrawLine(g, startX, startY, endX, endY);
                    }
                }
                // Draw representative lines
                g.setColor(Color.RED);
                for(int i=0; i<clusterRepresentativeTrajectoryAL.size();i++) {
                    for(int j=0; j<clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().size()-2; j++) {
                        //for(int j=0; j<clusterRepresentativeTrajectoryAL.get(i).getClusterPointArray().size()-2; j++) {


                        int startX = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j).getM_coordinate(0);
                        int startY = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j).getM_coordinate(1);
                        int endX = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j+1).getM_coordinate(0);
                        int endY = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j+1).getM_coordinate(1);


                        //g2.drawLine(startX, startY, endX, endY);
                        customDrawLine(g, startX, startY, endX, endY);
                    }
                }
            }
            // if ()

        }

        public void customDrawLine(Graphics g,
                                   double x1, double y1,
                                   double x2, double y2) {

            /*
            double scaleX = (double) getWidth() / (xR - xL);
            double scaleY = (double) getHeight() / (yL - yR);

            double translateX = (double) - xL*getWidth()/(xR - xL);
            double translateY = (double) - yR * getHeight()/(yL-yR);

            int transformX1 = (int) (scaleX * x1 + translateX);
            int transformY1 = (int) (scaleY * y1 + translateY);
            int transformX2 = (int) (scaleX * x2 + translateX);
            int transformY2 = (int) (scaleY * y2 + translateY);
            */

            int transformX1 = (int) (x1 * getWidth() / (xR - xL) - xL*getWidth()/(xR - xL));
            int transformY1 = (int) (y1 * getHeight() / (yL - yR) - yR * getHeight()/(yL-yR));
            int transformX2 = (int) (x2 * getWidth() / (xR - xL) - xL*getWidth()/(xR - xL));
            int transformY2 = (int) (y2 * getHeight() / (yL - yR) - yR * getHeight()/(yL-yR));

            g.drawLine(transformX1, transformY1, transformX2, transformY2);
        }


        /*
        @Override
        protected void paintComponent (Graphics g) {
            super.paintComponent(g);
            paint2D((Graphics2D)g);
        }

        protected void paint2D (Graphics2D g2) {
            AffineTransform tform = AffineTransform.getTranslateInstance( - xL*getWidth()/(xR-xL), - yR*getHeight()/(yL-yR));
            tform.scale(getWidth()/(xR-xL), getHeight()/(yL-yR));
            g2.setTransform(tform);

            //g2.setColor(Color.BLUE);  // NOTE -- so we can *see* something.

            if (trajectoryAL.isEmpty() != true
                    && clusterRepresentativeTrajectoryAL.isEmpty() != true) {
                // Draw trajectory lines
                g2.setColor(Color.GREEN);
                for (int i = 0; i < trajectoryAL.size();i++) {
                    for(int m=0; m<trajectoryAL.get(i).getM_pointArray().size()-2 ;m++) {
                        int startX = (int)trajectoryAL.get(i).getM_pointArray().get(m).getM_coordinate(0);
                        int startY = (int)trajectoryAL.get(i).getM_pointArray().get(m).getM_coordinate(1);
                        int endX = (int)trajectoryAL.get(i).getM_pointArray().get(m+1).getM_coordinate(0);
                        int endY = (int)trajectoryAL.get(i).getM_pointArray().get(m+1).getM_coordinate(1);

                        g2.drawLine(startX, startY, endX, endY);
                    }
                }
                // Draw representative lines
                g2.setColor(Color.RED);
                for(int i=0; i<clusterRepresentativeTrajectoryAL.size();i++) {
                    for(int j=0; j<clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().size()-2; j++) {
                    //for(int j=0; j<clusterRepresentativeTrajectoryAL.get(i).getClusterPointArray().size()-2; j++) {


                        int startX = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j).getM_coordinate(0);
                        int startY = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j).getM_coordinate(1);
                        int endX = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j+1).getM_coordinate(0);
                        int endY = (int)clusterRepresentativeTrajectoryAL.get(i).getM_PointArray().get(j+1).getM_coordinate(1);




                        int startX = (int)clusterRepresentativeTrajectoryAL.get(i).getClusterPointArray().get(j).getM_coordinate(0);
                        int startY = (int)clusterRepresentativeTrajectoryAL.get(i).getClusterPointArray().get(j).getM_coordinate(1);
                        int endX = (int)clusterRepresentativeTrajectoryAL.get(i).getClusterPointArray().get(j+1).getM_coordinate(0);
                        int endY = (int)clusterRepresentativeTrajectoryAL.get(i).getClusterPointArray().get(j+1).getM_coordinate(1);


                        g2.drawLine(startX, startY, endX, endY);
                    }
                }
            }

        }

        */
    }

    public Application() {
        initActions();
        initComponents();
    }

    private void cancel() {
        computationWorker.cancel(true);
    }

    private void initActions() {

        //New Actions
        /*
        openAction -> JButton "Open"
        startAction -> JButton "Start"
        stopAction -> JButton "Stop"
        drawAction -> JButton "Draw"
        */

        openAction = new AbstractAction("Open") {

            private static final long serialVersionUID = 4669650683189592364L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final File dir = new File(filePathTextField.getText()).getAbsoluteFile();
                final JFileChooser fileChooser = new JFileChooser(dir);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                final int option = fileChooser.showOpenDialog(Application.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    final File selected = fileChooser.getSelectedFile();
                    filePathTextField.setText(selected.getAbsolutePath());
                }
            }
        };

        startAction = new AbstractAction("Start") {

            private static final long serialVersionUID = 4669650683189592364L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                compute();
            }
        };

        stopAction = new AbstractAction("Stop") {

            private static final long serialVersionUID = 4669650683189592364L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                cancel();
            }
        };

        drawAction = new AbstractAction("Draw") {

            private static final long serialVersionUID = 4669650683189592364L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                //TODO
            }
        };

    }

    private void initComponents() {

        setLayout(new GridBagLayout());



        // New Element
        /*
        JButton "Open" | JButton "Start" | JButton "Stop" | JButton "Draw"
        -----
        JLabel "Path:" | JTextField
        -----
        JPanel > область для рисования
        -----
        JScrollPane > JTextArea > сообщения о процессе работы
        */

        //JButton "Open"
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JButton(openAction), constraints);

        //JButton "Start"
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JButton(startAction), constraints);

        //JButton "Stop"
        stopButton = new JButton(stopAction);
        stopButton.setEnabled(false);
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(stopButton, constraints);

        //JButton "Draw" (disabled)
        drawButton = new JButton(drawAction);
        drawButton.setEnabled(false);
        constraints = new GridBagConstraints();
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        //add(new JButton(drawAction), constraints);
        add(drawButton, constraints);

        //JLabel "Path:"
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JLabel("Path: "), constraints);

        filePathTextField = new JTextField();
        //filePathTextField.setText("C:\\FILES\\Matlab and C++\\GeoDATA\\TrajectoryData");
        filePathTextField.setText("C:\\FILES\\Matlab and C++\\GeoDATA\\TrajectoryData\\deer_1995.tra");
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(filePathTextField, constraints);

        //JPanel > область для рисования
        //drawPanel = new JPanel();
        // custom JComponent DrawArea
        drawPanel = new DrawArea(messagesTextArea);
        //drawPanel.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.weighty = 0.5;
        constraints.fill = GridBagConstraints.BOTH;
        add(drawPanel, constraints);
        //add(new JPanel(), constraints);

        //JScrollPane > JTextArea > сообщения о процессе работы
        messagesTextArea = new JTextArea();
        messagesTextArea.setEditable(false);
        messagesTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        messagesTextArea.setLineWrap(true);       // wrap line
        messagesTextArea.setWrapStyleWord(true);  // wrap line at word boundary

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.weighty = 0.5;
        constraints.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(messagesTextArea), constraints);

    }


    private void compute() {

        //final File file = new File(filePathTextField.getText());

        messagesTextArea.setText("Welcome message!\n");

        computationWorker = new ComputationWorker(filePathTextField.getText(), messagesTextArea, drawPanel);

        computationWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                switch (event.getPropertyName()) {
                    case "progress":
                        break;
                    case "state":
                        switch ((StateValue) event.getNewValue()) {
                            case DONE:
                                computationWorker = null;
                                break;
                            case STARTED:
                            case PENDING:
                                break;
                        }
                        break;
                }
            }
        });

        // Very important. Start up working thread.
        computationWorker.execute();
    }

}
