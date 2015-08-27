package TraClusterAlgorithm;

/**
 * Created by home on 24.08.2015.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.lang.String;
import java.util.concurrent.ExecutionException;
import java.util.Locale;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import java.nio.file.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

// import files form TraClusterDoc.java
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

//import TraClusterAlgorithm.TraClusterDoc.Parameter;
//import TraClusterAlgorithm.Cluster.ResultContainer;
import TraClusterAlgorithm.Application.DrawArea;

public class ComputationWorker extends SwingWorker<Integer, String> {

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while computing");
        }
    }


    /** Path to the file with trajectory data */
    private final String filePath;

    /** The text area where messages are written. */
    private final JTextArea messagesTextArea;

    // Here lines are drawn
    private final DrawArea drawPanel;

    // properties from TraClusterDoc
    public int m_nDimensions;
    public int m_nTrajectories;
    public int m_nClusters;
    public double m_clusterRatio;
    public int m_maxNPoints;
    public ArrayList<Trajectory> m_trajectoryList;
    public ArrayList<Cluster> m_clusterList;

    public class Parameter {
        double epsParam;
        int minLnsParam;
    }



    /**
     * Creates an instance of the worker
     *
     * @param filePath
     *          Path to the file with trajectory data
     * @param messagesTextArea
     *          The text area where messages are written
     */
    public ComputationWorker(final String filePath,
                             final JTextArea messagesTextArea,
                             final DrawArea drawPanel) {
        this.filePath = filePath;
        this.messagesTextArea = messagesTextArea;
        this.drawPanel = drawPanel;

        m_nTrajectories = 0;
        m_nClusters = 0;
        m_clusterRatio = 0.0;
        m_trajectoryList = new ArrayList<Trajectory>();
        m_clusterList = new ArrayList<Cluster>();
    }

    // TODO
    @Override
    protected Integer doInBackground() throws Exception {


        publish("Entering working thread.\n");

        // TraClusterAlgorithm

        Parameter p = this.onEstimateParameter();

        publish("Based on the algorithm, the suggested parameters are:\n" +
                "eps:" + p.epsParam + "  minLns:" + p.minLnsParam);

        // inputFilePath
        this.onOpenDocument(this.filePath);

        //#TEMP
        //publish("End of onOpenDocument()");

        //
        Path objPath = Paths.get(this.filePath);

        /*
        String outputFilePath = objPath.getRoot().toString() +
                objPath.getParent().toString().substring(1) +
                "test" +
                objPath.getFileName().toString().split(Pattern.quote("."))[0] +
                ".txt";
        */
        String outputFilePath = objPath.getParent().toString() +
                "\\test" +
                objPath.getFileName().toString().split(Pattern.quote("."))[0] +
                ".txt";

        publish("\nResulting outputFilePath: " + outputFilePath);

        /*
       * In order to respond to the cancellations, we need to check whether this thread (the worker thread) was
       * interrupted or not. If the thread was interrupted, then we simply throw an InterruptedException to indicate
       * that the worker thread was cancelled.
       */
        ComputationWorker.failIfInterrupted();

        // outputFilePath, eps, minLns
        this.onClusterGenerate(outputFilePath, p.epsParam, p.minLnsParam);

        return Integer.valueOf(0);

        //return new ResultContainer(m_trajectoryList, m_clusterList);

    }

    @Override
    protected void process(final List<String> chunks) {
        // Updates the messages text area
        for (final String string : chunks) {
            messagesTextArea.append(string);
            messagesTextArea.append("\n");
        }
    }

    @Override
    protected void done() {
        /*
        try {
            // Retrieve the return value of doInBackground.
            //status = get();
            //statusLabel.setText('Completed with status: ' + status);

            //
            drawPanel.setTrajectory(m_trajectoryList);
            drawPanel.setClusterRepresentativeTrajectory(m_clusterList);

        } catch (InterruptedException e) {
            // This is thrown if the thread's interrupted.
        } catch (ExecutionException e) {

            // This is thrown if we throw an exception
            // from doInBackground.
        }
        */

        drawPanel.setTrajectory(m_trajectoryList);
        drawPanel.setClusterRepresentativeTrajectory(m_clusterList);
    }

    // Methods from TraClusterDoc.java
    // I need to move methods for publish() to work properly

    boolean onOpenDocument(String inputFileName) {

        //#TEMP
        //publish("Entering onOpenDocument()");

        int nDimensions = 2;		// default dimension = 2
        int nTrajectories = 0;
        int nTotalPoints = 0;		//no use
        int trajectoryId;
        int nPoints;
        double value;


        DataInputStream in;
        BufferedReader inBuffer = null;
        try {

            //#TEMP
            //publish("onOpenDocument() Entering try");

            in = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(inputFileName)));

            inBuffer = new BufferedReader(
                    new InputStreamReader(in));

            nDimensions = Integer.parseInt(inBuffer.readLine());			// the number of dimensions
            m_nDimensions = nDimensions;
            nTrajectories = Integer.parseInt(inBuffer.readLine());		// the number of trajectories
            m_nTrajectories = nTrajectories;

            m_maxNPoints = -1;		// initialize for comparison


            // the trajectory Id, the number of points, the coordinate of a point ...
            for(int i=0; i<nTrajectories; i++) {

                String str = inBuffer.readLine();

                Scanner sc = new Scanner(str);
                sc.useLocale(Locale.US); // Very important setting! Without it we can't read double

                //source: http://stackoverflow.com/questions/5929120/nextdouble-throws-an-exception-when-i-enter-a-double
                //Scanner scan = new Scanner(System.in);
                //scan.useLocale(Locale.US);

                trajectoryId = sc.nextInt();		//trajectoryID
                nPoints = sc.nextInt();				//nubmer of points in the trajectory

                //#TEMP
                //publish("nDimensions-> " + nDimensions);
                //publish("nTrajectories-> " + nTrajectories);
                //publish("trajectoryId-> " + trajectoryId);
                //publish("nPoints-> " + nPoints);

                if(nPoints > m_maxNPoints) m_maxNPoints = nPoints;
                nTotalPoints += nPoints;

                Trajectory pTrajectoryItem = new Trajectory(trajectoryId, nDimensions);

                //#TEMP
                //publish("onOpenDocument() Trajectory-> " + i);

                for(int j=0; j<nPoints; j++) {

                    CMDPoint point = new CMDPoint(nDimensions);   // initialize the CMDPoint class for each point

                    for(int k =0; k< nDimensions; k++) {

                        value = sc.nextDouble();
                        point.setM_coordinate(k, value);

                        //#TEMP
                        //publish("onOpenDocument() (1) dimension-> " + k);
                        //#TEMP
                        //publish("onOpenDocument() (1) dimension value-> " + value);
                        //#TEMP
                        //publish("onOpenDocument() (2) dimension-> " + k);
                    }

                    pTrajectoryItem.addPointToArray(point);

                }

                m_trajectoryList.add(pTrajectoryItem);

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            publish("Unable to open input file");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                inBuffer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    boolean onClusterGenerate(String clusterFileName, double epsParam, int minLnsParam) {

        //#TEMP
        //publish("Entering onClusterGenerate()\n");

        ClusterGen generator = new ClusterGen(this);

        if(m_nTrajectories ==0) {
            //System.out.println("Load a trajectory data set first");
            publish("Load a trajectory data set first");
        }

        // FIRST STEP: Trajectory Partitioning
        if (!generator.partitionTrajectory())
        {
            //System.out.println("Unable to partition a trajectory\n");
            publish("Unable to partition a trajectory\n");
            return false;
        }

        //#TEMP
        //publish("End of generator.partitionTrajectory()\n");

        // SECOND STEP: Density-based Clustering
        if (!generator.performDBSCAN(epsParam, minLnsParam))
        {
            //System.out.println("Unable to perform the DBSCAN algorithm\n");
            publish("Unable to perform the DBSCAN algorithm\n");
            return false;
        }

        //#TEMP
        //publish("End of generator.performDBSCAN(epsParam, minLnsParam)\n");

        // THIRD STEP: Cluster Construction
        if (!generator.constructCluster())
        {
            //System.out.println( "Unable to construct a cluster\n");
            publish("Unable to construct a cluster\n");
            return false;
        }

        //#TEMP
        //publish("End of generator.constructCluster()\n");

        //publish("End of Computations\n");
        publish("\nm_clusterList.size(): " + m_clusterList.size());
        publish("m_trajectoryList.size(): " + m_trajectoryList.size());

        /*
        // Вывод информации о характерных траекториях
        for(int i=0; i<m_clusterList.size(); i++) {
            //m_clusterList.
            //System.out.println(m_clusterList.get(i).getM_clusterId());
            publish(String.valueOf(m_clusterList.get(i).getM_clusterId()));

            for(int j=0; j<m_clusterList.get(i).getM_PointArray().size(); j++) {

                double x = m_clusterList.get(i).getM_PointArray().get(j).getM_coordinate(0);
                double y = m_clusterList.get(i).getM_PointArray().get(j).getM_coordinate(1);
                //System.out.print("   "+ x +" "+ y +"   ");
                publish("   " + x + " " + y + "   ");
            }
            //System.out.println();
        }
        */

        FileOutputStream fos = null;
        BufferedWriter bw = null;
        OutputStreamWriter osw = null;

        try {
            fos = new FileOutputStream(clusterFileName);
            osw = new OutputStreamWriter(fos);
            bw = new BufferedWriter(osw);

            bw.write("epsParam:"+epsParam +"   minLnsParam:"+minLnsParam);

            for(int i=0; i<m_clusterList.size(); i++) {
                //m_clusterList.
                //System.out.println(m_clusterList.get(i).getM_clusterId());
                bw.write("\nclusterID: "+ m_clusterList.get(i).getM_clusterId()+"  Points Number:  "+m_clusterList.get(i).getM_PointArray().size()+"\n");
                for(int j=0; j<m_clusterList.get(i).getM_PointArray().size(); j++) {

                    double x = m_clusterList.get(i).getM_PointArray().get(j).getM_coordinate(0);
                    double y = m_clusterList.get(i).getM_PointArray().get(j).getM_coordinate(1);
                    //System.out.print("   "+ x +" "+ y +"   ");
                    bw.write(x+" "+y+"   ");
                }
                //System.out.println();
            }

            publish("Results were written into " + clusterFileName);

        } catch (FileNotFoundException e) {
            publish("onClusterGenerate() File not found");
            e.printStackTrace();
        } catch (IOException e) {
            publish("onClusterGenerate() IOException");
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return true;
    }


    Parameter onEstimateParameter()
    {
        Parameter p = new Parameter();

        ClusterGen generator = new ClusterGen(this);

        if (!generator.partitionTrajectory())
        {
            //System.out.println("Unable to partition a trajectory\n");
            publish("Unable to partition a trajectory\n");
            return null;
        }

        //if (!generator.estimateParameterValue(epsParam, minLnsParam))
        if (!generator.estimateParameterValue(p))
        {
            //System.out.println("Unable to calculate the entropy\n");
            publish("Unable to calculate the entropy\n");
            return null;
        }

        return p;
    }

}
