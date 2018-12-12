package networktools;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.swing.*;

/**
 * IPv4 Scanner with Port Scanner. 
 * 
 * ToDo Comments: 
 * Make a nicer GUI (Spacing around the components)
 * Exception on Port Scanner.
 * TextField; remove starting text and put it in labels
 * 
 * @author Michael
 */
public class NetworkTools {

    public static void main(String[] args) {
        JFrame mainWindow = new JFrame("Network Tools");
        JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
        String[] tabTitles = {"IP Scanner", "IP Lookup", "Settings"};
        
        for(int i = 0; i < tabTitles.length; i++)
            tabPane.addTab(tabTitles[i], makePanel(tabTitles[i], i));
        
        mainWindow.getContentPane().setLayout(new GridLayout(1,1));
        mainWindow.getContentPane().add(tabPane);
        mainWindow.setSize(400,400);  
        //mainWindow.setLayout(null);  
        mainWindow.setVisible(true);  
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static JPanel makePanel(String title, int i){
        JPanel panel = new JPanel();
        
        switch(i){
            case 0: panel = IPScannerGUI(panel);
                break;
            default: panel.add(new JLabel(title + "not implemented"));
                break;
        }
        
        panel.setLayout(null);
        return panel;
    }
    
    public static JPanel IPScannerGUI(JPanel panel){
        //Change what is in the Text Fields to labels and make the TextFields empty
        JTextField t1 = new JTextField("Starting IP");
        JTextField t2 = new JTextField("Ending IP");
        JTextArea area = new JTextArea();
        JTextField port1 = new JTextField("Starting Port");;
        JTextField port2 = new JTextField("Ending Port");
        t1.setBounds(25,50,100,20);
        t2.setBounds(200,50,100,20);
        JCheckBox portScan = new JCheckBox("Scan Ports?", false);
        portScan.setBounds(100,75,100,20);

        port1.setBounds(25,100,100,20);
        port2.setBounds(200,100,100,20);

        area.setEditable(false);
        JScrollPane sp = new JScrollPane(area);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setBounds(25,200,300,100);
        JButton b=new JButton("Scan Range");  
        b.setBounds(100,150,150,30);
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                IPScan(t1.getText(), t2.getText(), area, sp, portScan.isSelected(), port1.getText(), port2.getText());
            }
            });
        
        panel.add(portScan);
        panel.add(b);
        panel.add(t1);
        panel.add(t2);
        panel.add(sp);
        panel.add(port1);
        panel.add(port2);
        return panel;
    }
    
    public static void IPScan(String ipAddressStart, String ipAddressEnd, JTextArea area, JScrollPane sp, Boolean portSc, String port1, String port2){
        int ipTimeOut = 1000, portTimeOut =1000;
        int portS=0, portE=0;
        area.setText(null);
        if(portSc){
            portS = Integer.parseInt(port1);
            portE = Integer.parseInt(port2);
        }
        //Divids the IP address into sections and changes them from string to ints
        int ipStartA, ipStartB, ipStartC, ipStartD;
        int ipEndA, ipEndB, ipEndC, ipEndD;
        int temp, temp2;
        //Starting IP Address
        temp = ipAddressStart.indexOf(".");
        ipStartA = Integer.parseInt(ipAddressStart.substring(0, temp));
        temp2 = ipAddressStart.indexOf(".", temp+1);
        ipStartB = Integer.parseInt(ipAddressStart.substring(temp+1, temp2));
        temp = ipAddressStart.indexOf(".", temp2+1);
        ipStartC = Integer.parseInt(ipAddressStart.substring(temp2+1, temp));
        ipStartD = Integer.parseInt(ipAddressStart.substring(temp+1, ipAddressStart.length()));
        //Ending IP Address
        temp = ipAddressEnd.indexOf(".");
        ipEndA = Integer.parseInt(ipAddressEnd.substring(0, temp));
        temp2 = ipAddressEnd.indexOf(".", temp+1);
        ipEndB = Integer.parseInt(ipAddressEnd.substring(temp+1, temp2));
        temp = ipAddressEnd.indexOf(".", temp2+1);
        ipEndC = Integer.parseInt(ipAddressEnd.substring(temp2+1, temp));
        ipEndD = Integer.parseInt(ipAddressEnd.substring(temp+1, ipAddressEnd.length()));

        //loop through each IP address inbetween the start and end
        for(;ipStartA<=ipEndA; ipStartA++){
            for(;ipStartB<=ipEndB; ipStartB++){
                for(;ipStartC <= ipEndC; ipStartC++){
                    for(;ipStartD <= ipEndD; ipStartD++){
                        try{
                            String ipAdd = (ipStartA + "." + ipStartB + "." + ipStartC + "." + ipStartD);
                            InetAddress inet = InetAddress.getByName(ipAdd);
                            if (inet.isReachable(ipTimeOut)){
                                area.append(ipAdd + " can be reached.\n");
                                if(portSc)
                                    for(; portS <= portE; portS++){
                                        try {
                                            Socket socket = new Socket();
                                            socket.connect(new InetSocketAddress(ipAdd, portS), portTimeOut);
                                            socket.close();
                                            area.append("Port " + portS + " is open.\n");
                                        } catch (Exception ex) {
                                            //need to serpate catch to "Connection refused: connect" and print them and not "connect timed out"
                                            area.append("Port " + portS + " Exception: " + ex.getMessage() + "\n");
                                        }
                                    }
                            } else 
                                area.append(ipAdd + " can not reached.\n");
                        }  catch ( Exception e ) {
                            System.out.println("Exception:" + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}